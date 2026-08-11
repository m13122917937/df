package com.ruoyi.subsidy.facade.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.subsidy.convert.GbOrderConvert;
import com.ruoyi.subsidy.domain.GbCategory;
import com.ruoyi.subsidy.domain.GbOrder;
import com.ruoyi.subsidy.domain.GbOrderAddress;
import com.ruoyi.subsidy.domain.GbOrderItem;
import com.ruoyi.subsidy.domain.GbPayment;
import com.ruoyi.subsidy.domain.GbShipment;
import com.ruoyi.subsidy.domain.GbProduct;
import com.ruoyi.subsidy.domain.GbProductSku;
import com.ruoyi.subsidy.facade.IGbOrderFacade;
import com.ruoyi.subsidy.model.bo.GbOrderBO;
import com.ruoyi.subsidy.model.bo.GbPaymentOrderBO;
import com.ruoyi.subsidy.model.param.GbOrderCreateParam;
import com.ruoyi.subsidy.model.param.GbShipmentParam;
import com.ruoyi.subsidy.service.GbCategoryService;
import com.ruoyi.subsidy.service.GbOrderService;
import com.ruoyi.subsidy.service.GbProductService;
import com.ruoyi.subsidy.service.GbProductSkuService;
import com.ruoyi.subsidy.service.GbOrderItemService;
import com.ruoyi.subsidy.service.GbPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 国补订单领域实现。
 */
@Component
@RequiredArgsConstructor
public class GbOrderFacade implements IGbOrderFacade {
    private final GbOrderService orderService;
    private final GbProductSkuService skuService;
    private final GbProductService productService;
    private final GbCategoryService categoryService;
    private final GbPaymentService paymentService;
    private final GbOrderItemService orderItemService;

    @Override
    public GbOrderBO create(final GbOrderCreateParam param) {
        validateCreateParam(param);
        GbProductSku sku = skuService.getById(param.getSkuId());
        Assert.notNull(sku, "SKU不存在");
        Assert.isTrue(Integer.valueOf(1).equals(sku.getStatus()), "SKU已下架");
        Assert.isTrue(sku.getStockQuantity() > 0, "商品库存不足");
        GbProduct product = productService.getById(sku.getProductId());
        Assert.notNull(product, "商品不存在");
        Assert.isTrue(Integer.valueOf(1).equals(product.getStatus()), "商品已下架");
        GbCategory category = categoryService.getById(product.getCategoryId());
        Assert.notNull(category, "商品分类不存在");
        Assert.isTrue(Integer.valueOf(1).equals(category.getStatus()), "商品分类已停用");
        Assert.isTrue(isProvinceSupported(category.getSaleProvinces(), param.getProvinceName()), "收货省份暂不支持配送");
        BigDecimal totalAmount = sku.getOriginalPrice().multiply(BigDecimal.valueOf(param.getQuantity()));
        BigDecimal rateDiscount = totalAmount.multiply(category.getDiscountRate());
        BigDecimal discountAmount = rateDiscount.min(category.getDiscountCapAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal payAmount = totalAmount.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
        String orderNo = "GB" + IdUtil.fastSimpleUUID();
        GbOrder order = new GbOrder().setOrderNo(orderNo).setMemberId(param.getMemberId())
                .setTotalAmount(totalAmount).setDiscountAmount(discountAmount).setPayAmount(payAmount)
                .setExpireTime(DateUtil.offsetMinute(DateUtil.date(), 15));
        GbOrderItem item = new GbOrderItem().setSkuId(sku.getId()).setProductName(product.getProductName())
                .setSpecName(sku.getSpecName()).setQuantity(param.getQuantity()).setUnitPrice(sku.getOriginalPrice())
                .setDiscountRate(category.getDiscountRate()).setDiscountCapAmount(category.getDiscountCapAmount())
                .setDiscountAmount(discountAmount).setPayAmount(payAmount);
        GbOrderAddress address = new GbOrderAddress().setReceiverName(param.getReceiverName())
                .setReceiverPhone(param.getReceiverPhone()).setProvinceName(param.getProvinceName())
                .setCityName(param.getCityName()).setDistrictName(param.getDistrictName())
                .setDetailAddress(param.getDetailAddress());
        GbPayment payment = new GbPayment().setPaymentNo("PAY" + IdUtil.fastSimpleUUID()).setAmount(payAmount);
        orderService.createOrder(order, item, address, payment);
        return GbOrderConvert.INSTANCE.toBO(order);
    }

    @Override
    public GbPaymentOrderBO getPendingPayment(final Long memberId, final String orderNo) {
        GbOrder order = orderService.getByMemberAndOrderNo(memberId, orderNo);
        Assert.notNull(order, "订单不存在");
        Assert.isTrue(com.ruoyi.subsidy.model.consts.GbOrderStatus.PENDING_PAY.equals(order.getOrderStatus()), "订单不可支付");
        Assert.isTrue(order.getExpireTime().after(DateUtil.date()), "订单已过期");
        GbPayment payment = paymentService.getByOrderId(order.getId());
        Assert.notNull(payment, "支付单不存在");
        GbPaymentOrderBO result = new GbPaymentOrderBO();
        result.setOrderNo(order.getOrderNo());
        result.setPaymentNo(payment.getPaymentNo());
        result.setMemberId(order.getMemberId());
        result.setPayAmount(order.getPayAmount());
        result.setOrderStatus(order.getOrderStatus());
        return result;
    }

    @Override
    public boolean markWechatPaid(final String paymentNo, final String transactionId, final BigDecimal paidAmount) {
        GbPayment payment = paymentService.getByPaymentNo(paymentNo);
        Assert.notNull(payment, "支付单不存在");
        Assert.isTrue(payment.getAmount().compareTo(paidAmount) == 0, "支付金额不匹配");
        GbOrder order = orderService.getById(payment.getOrderId());
        Assert.notNull(order, "订单不存在");
        GbOrderItem item = orderItemService.getFirstByOrderId(order.getId());
        Assert.notNull(item, "订单商品不存在");
        return orderService.markPaid(order, payment, item, transactionId);
    }

    @Override
    public List<GbOrderBO> listByMemberId(final Long memberId) {
        return orderService.listByMemberId(memberId).stream().map(GbOrderConvert.INSTANCE::toBO)
                .collect(Collectors.toList());
    }

    @Override
    public GbOrderBO getByMemberAndOrderNo(final Long memberId, final String orderNo) {
        GbOrder order = orderService.getByMemberAndOrderNo(memberId, orderNo);
        Assert.notNull(order, "订单不存在");
        return GbOrderConvert.INSTANCE.toBO(order);
    }

    @Override
    public void ship(final GbShipmentParam param) {
        Assert.notBlank(param.getOrderNo(), "订单号不能为空");
        Assert.notBlank(param.getLogisticsCompany(), "物流公司不能为空");
        Assert.notBlank(param.getTrackingNo(), "运单号不能为空");
        GbOrder order = orderService.getByOrderNo(param.getOrderNo());
        Assert.notNull(order, "订单不存在");
        GbShipment shipment = new GbShipment().setLogisticsCompany(param.getLogisticsCompany()).setTrackingNo(param.getTrackingNo());
        Assert.isTrue(orderService.ship(order, shipment), "订单状态不允许发货");
    }

    @Override
    public void confirmReceived(final Long memberId, final String orderNo) {
        GbOrder order = orderService.getByMemberAndOrderNo(memberId, orderNo);
        Assert.notNull(order, "订单不存在");
        Assert.isTrue(orderService.confirmReceived(order), "订单状态不允许确认收货");
    }

    @Override
    public int completeExpiredShipments() {
        return orderService.completeShippedBefore(DateUtil.offsetDay(DateUtil.date(), -7));
    }

    @Override
    public List<GbOrderBO> listForAdmin(final String orderStatus) {
        return orderService.listForAdmin(orderStatus).stream().map(GbOrderConvert.INSTANCE::toBO)
                .collect(Collectors.toList());
    }

    @Override
    public void cancel(final Long memberId, final String orderNo) {
        GbOrder order = orderService.getByMemberAndOrderNo(memberId, orderNo);
        Assert.notNull(order, "订单不存在");
        Assert.isTrue(orderService.cancelIfPending(order), "订单状态不允许取消");
    }

    private void validateCreateParam(final GbOrderCreateParam param) {
        Assert.notNull(param.getMemberId(), "会员不能为空");
        Assert.notNull(param.getSkuId(), "SKU不能为空");
        Assert.isTrue(param.getQuantity() != null && param.getQuantity() > 0, "购买数量必须大于0");
        Assert.notBlank(param.getReceiverName(), "收货人不能为空");
        Assert.notBlank(param.getReceiverPhone(), "收货电话不能为空");
        Assert.notBlank(param.getProvinceName(), "收货省份不能为空");
        Assert.notBlank(param.getCityName(), "收货城市不能为空");
        Assert.notBlank(param.getDistrictName(), "收货地区不能为空");
        Assert.notBlank(param.getDetailAddress(), "详细地址不能为空");
    }

    /**
     * 判断收货省份是否在分类配置的销售范围内。
     *
     * @param saleProvinces 分类销售省份 JSON 数组
     * @param provinceName 收货省份名称
     * @return 是否允许配送
     */
    private boolean isProvinceSupported(final String saleProvinces, final String provinceName) {
        List<String> provinces = JacksonUtil.parseList(saleProvinces, String.class);
        return CollUtil.isNotEmpty(provinces) && CollUtil.contains(provinces, provinceName);
    }
}
