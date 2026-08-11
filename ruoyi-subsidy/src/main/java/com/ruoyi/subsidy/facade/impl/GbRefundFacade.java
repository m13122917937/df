package com.ruoyi.subsidy.facade.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.ruoyi.subsidy.domain.GbOrder;
import com.ruoyi.subsidy.domain.GbOrderItem;
import com.ruoyi.subsidy.domain.GbPayment;
import com.ruoyi.subsidy.domain.GbRefund;
import com.ruoyi.subsidy.facade.IGbRefundFacade;
import com.ruoyi.subsidy.model.bo.GbRefundBO;
import com.ruoyi.subsidy.model.bo.GbRefundPaymentBO;
import com.ruoyi.subsidy.model.consts.GbOrderStatus;
import com.ruoyi.subsidy.model.param.GbRefundApplyParam;
import com.ruoyi.subsidy.service.GbOrderService;
import com.ruoyi.subsidy.service.GbPaymentService;
import com.ruoyi.subsidy.service.GbRefundService;
import com.ruoyi.subsidy.service.GbOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** 国补退款领域实现。 */
@Component
@RequiredArgsConstructor
public class GbRefundFacade implements IGbRefundFacade {
    private final GbOrderService orderService;
    private final GbPaymentService paymentService;
    private final GbRefundService refundService;
    private final GbOrderItemService orderItemService;

    @Override
    public GbRefundBO apply(final GbRefundApplyParam param) {
        Assert.notNull(param.getMemberId(), "会员不能为空");
        Assert.notBlank(param.getOrderNo(), "订单号不能为空");
        GbOrder order = orderService.getByMemberAndOrderNo(param.getMemberId(), param.getOrderNo());
        Assert.notNull(order, "订单不存在");
        Assert.isTrue(GbOrderStatus.PAID.equals(order.getOrderStatus()), "仅未发货订单可以申请退款");
        GbPayment payment = paymentService.getByOrderId(order.getId());
        Assert.notNull(payment, "支付单不存在");
        GbRefund refund = new GbRefund().setRefundNo("REF" + IdUtil.fastSimpleUUID())
                .setAmount(payment.getAmount()).setReason(param.getReason());
        Assert.isTrue(orderService.applyRefund(order, refund), "退款申请状态异常");
        GbRefundBO result = new GbRefundBO();
        result.setRefundNo(refund.getRefundNo());
        result.setOrderNo(order.getOrderNo());
        result.setAmount(refund.getAmount());
        result.setRefundStatus(refund.getRefundStatus());
        return result;
    }

    @Override
    public GbRefundPaymentBO approve(final String refundNo) {
        GbRefund refund = refundService.getByRefundNo(refundNo);
        Assert.notNull(refund, "退款单不存在");
        GbOrder order = orderService.getById(refund.getOrderId());
        Assert.notNull(order, "订单不存在");
        GbPayment payment = paymentService.getByOrderId(order.getId());
        Assert.notNull(payment, "支付单不存在");
        Assert.isTrue(orderService.startRefund(order, refund), "退款单状态异常");
        GbRefundPaymentBO result = new GbRefundPaymentBO();
        result.setRefundNo(refund.getRefundNo());
        result.setPaymentNo(payment.getPaymentNo());
        result.setWechatTransactionId(payment.getWechatTransactionId());
        result.setAmount(refund.getAmount());
        result.setReason(refund.getReason());
        return result;
    }

    @Override
    public void markWechatRefundFailed(final String refundNo) {
        GbRefund refund = refundService.getByRefundNo(refundNo);
        Assert.notNull(refund, "退款单不存在");
        GbOrder order = orderService.getById(refund.getOrderId());
        Assert.notNull(order, "订单不存在");
        orderService.markRefundFailed(order, refund);
    }

    @Override
    public boolean completeWechatRefund(final String refundNo, final String wechatRefundId) {
        GbRefund refund = refundService.getByRefundNo(refundNo);
        Assert.notNull(refund, "退款单不存在");
        GbOrder order = orderService.getById(refund.getOrderId());
        Assert.notNull(order, "订单不存在");
        GbOrderItem item = orderItemService.getFirstByOrderId(order.getId());
        Assert.notNull(item, "订单商品不存在");
        return orderService.completeRefund(order, refund, item, wechatRefundId);
    }

    @Override
    public List<GbRefund> listForAdmin(final String refundStatus) {
        return refundService.listForAdmin(refundStatus);
    }
}
