package com.ruoyi.subsidy.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.subsidy.domain.GbOrder;
import com.ruoyi.subsidy.domain.GbOrderAddress;
import com.ruoyi.subsidy.domain.GbOrderItem;
import com.ruoyi.subsidy.domain.GbPayment;
import com.ruoyi.subsidy.domain.GbProductSku;
import com.ruoyi.subsidy.domain.GbStockLog;
import com.ruoyi.subsidy.mapper.GbOrderAddressMapper;
import com.ruoyi.subsidy.mapper.GbOrderItemMapper;
import com.ruoyi.subsidy.mapper.GbOrderMapper;
import com.ruoyi.subsidy.mapper.GbPaymentMapper;
import com.ruoyi.subsidy.mapper.GbProductSkuMapper;
import com.ruoyi.subsidy.mapper.GbStockLogMapper;
import com.ruoyi.subsidy.mapper.GbRefundMapper;
import com.ruoyi.subsidy.mapper.GbShipmentMapper;
import com.ruoyi.subsidy.domain.GbRefund;
import com.ruoyi.subsidy.domain.GbShipment;
import com.ruoyi.subsidy.model.consts.GbOrderStatus;
import com.ruoyi.subsidy.model.consts.GbPaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 国补订单持久化领域服务。
 */
@Service
@RequiredArgsConstructor
public class GbOrderService extends ServiceImpl<GbOrderMapper, GbOrder> {
    private final GbOrderItemMapper orderItemMapper;
    private final GbOrderAddressMapper orderAddressMapper;
    private final GbPaymentMapper paymentMapper;
    private final GbProductSkuMapper skuMapper;
    private final GbStockLogMapper stockLogMapper;
    private final GbRefundMapper refundMapper;
    private final GbShipmentMapper shipmentMapper;

    /**
     * 按会员和订单号查询订单。
     *
     * @param memberId 会员ID
     * @param orderNo 订单号
     * @return 订单
     */
    public GbOrder getByMemberAndOrderNo(final Long memberId, final String orderNo) {
        return baseMapper.selectByMemberAndOrderNo(memberId, orderNo);
    }

    /** 统计待发货订单数。 */
    public Long countPendingShipment() {
        return baseMapper.countPendingShipment();
    }

    /** 查询会员订单列表。 */
    public List<GbOrder> listByMemberId(final Long memberId) {
        return baseMapper.selectByMemberId(memberId);
    }

    /** 按订单号查询订单。 */
    public GbOrder getByOrderNo(final String orderNo) {
        return baseMapper.selectByOrderNo(orderNo);
    }

    /** 创建退款申请并锁定订单状态。 */
    @Transactional(rollbackFor = Exception.class)
    public boolean applyRefund(final GbOrder order, final GbRefund refund) {
        int changed = baseMapper.markRefundApplying(order.getId());
        if (changed != 1) {
            return false;
        }
        refundMapper.insert(refund.setOrderId(order.getId()).setRefundStatus("APPLYING").setCreateTime(DateUtil.date()));
        return true;
    }

    /** 审核通过退款申请并标记为退款中。 */
    @Transactional(rollbackFor = Exception.class)
    public boolean startRefund(final GbOrder order, final GbRefund refund) {
        int refundChanged = refundMapper.updateStatus(refund.getId(), "APPLYING", "REFUNDING", null);
        if (refundChanged != 1) {
            return false;
        }
        int orderChanged = baseMapper.updateRefundStatus(order.getId(), GbOrderStatus.REFUND_APPLYING, GbOrderStatus.REFUNDING);
        if (orderChanged != 1) {
            throw new IllegalStateException("订单退款状态异常");
        }
        return true;
    }

    /** 将退款中订单标记为退款失败。 */
    @Transactional(rollbackFor = Exception.class)
    public void markRefundFailed(final GbOrder order, final GbRefund refund) {
        refundMapper.updateStatus(refund.getId(), "REFUNDING", "FAILED", null);
        baseMapper.updateRefundStatus(order.getId(), GbOrderStatus.REFUNDING, GbOrderStatus.REFUND_FAILED);
    }

    /** 完成退款、恢复库存并写入流水。 */
    @Transactional(rollbackFor = Exception.class)
    public boolean completeRefund(final GbOrder order, final GbRefund refund, final GbOrderItem item,
                                  final String wechatRefundId) {
        int refundChanged = refundMapper.updateStatus(refund.getId(), "REFUNDING", "REFUNDED", wechatRefundId);
        if (refundChanged != 1) {
            return false;
        }
        int orderChanged = baseMapper.updateRefundStatus(order.getId(), GbOrderStatus.REFUNDING, GbOrderStatus.REFUNDED);
        if (orderChanged != 1) {
            throw new IllegalStateException("订单退款状态异常");
        }
        skuMapper.adjustStock(item.getSkuId(), item.getQuantity());
        GbProductSku sku = skuMapper.selectById(item.getSkuId());
        stockLogMapper.insert(new GbStockLog().setSkuId(item.getSkuId()).setOrderId(order.getId())
                .setChangeQuantity(item.getQuantity()).setAfterQuantity(sku.getStockQuantity()).setChangeType("REFUND_RESTORE")
                .setRemark("微信退款成功恢复库存").setCreateTime(DateUtil.date()));
        return true;
    }

    /** 写入发货信息并更新订单状态。 */
    @Transactional(rollbackFor = Exception.class)
    public boolean ship(final GbOrder order, final GbShipment shipment) {
        int changed = baseMapper.markShippedIfPaid(order.getId());
        if (changed != 1) {
            return false;
        }
        shipmentMapper.insert(shipment.setOrderId(order.getId()).setShippedTime(DateUtil.date()).setCreateTime(DateUtil.date()));
        return true;
    }

    /** 确认已发货订单收货完成。 */
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmReceived(final GbOrder order) {
        return baseMapper.markCompletedIfShipped(order.getId()) == 1;
    }

    /** 批量自动完成发货满七天的订单。 */
    @Transactional(rollbackFor = Exception.class)
    public int completeShippedBefore(final Date completedBefore) {
        return baseMapper.completeShippedBefore(completedBefore);
    }

    /** 查询后台订单列表。 */
    public List<GbOrder> listForAdmin(final String orderStatus) {
        return baseMapper.selectAdminList(orderStatus);
    }

    /** 取消待支付订单。 */
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelIfPending(final GbOrder order) {
        return baseMapper.cancelIfPending(order.getId()) == 1;
    }

    /**
     * 创建待支付订单及其快照。
     *
     * @param order 订单
     * @param item 商品快照
     * @param address 地址快照
     * @param payment 支付单
     */
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(final GbOrder order, final GbOrderItem item,
                            final GbOrderAddress address, final GbPayment payment) {
        Date now = DateUtil.date();
        order.setOrderStatus(GbOrderStatus.PENDING_PAY).setCreateTime(now).setUpdateTime(now);
        save(order);
        orderItemMapper.insert(item.setOrderId(order.getId()));
        orderAddressMapper.insert(address.setOrderId(order.getId()));
        paymentMapper.insert(payment.setOrderId(order.getId()).setPaymentStatus(GbPaymentStatus.PENDING)
                .setCreateTime(now));
    }

    /**
     * 处理幂等的支付成功结果。
     *
     * @param order 订单
     * @param payment 支付单
     * @param item 商品快照
     * @param transactionId 微信交易号
     * @return 是否首次处理成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean markPaid(final GbOrder order, final GbPayment payment, final GbOrderItem item,
                            final String transactionId) {
        Date now = DateUtil.date();
        int orderChanged = baseMapper.markPaidIfPending(order.getId());
        if (orderChanged != 1) {
            return false;
        }
        int paymentChanged = paymentMapper.markPaidIfPending(payment.getId(), transactionId);
        if (paymentChanged != 1) {
            throw new IllegalStateException("支付单状态异常");
        }
        int changed = skuMapper.adjustStock(item.getSkuId(), -item.getQuantity());
        if (changed != 1) {
            throw new IllegalStateException("库存更新失败");
        }
        GbProductSku sku = skuMapper.selectById(item.getSkuId());
        stockLogMapper.insert(new GbStockLog().setSkuId(item.getSkuId()).setOrderId(order.getId())
                .setChangeQuantity(-item.getQuantity()).setAfterQuantity(sku.getStockQuantity()).setChangeType("PAYMENT_DEDUCT")
                .setRemark("微信支付成功扣减库存").setCreateTime(now));
        return true;
    }
}
