package com.ruoyi.subsidy.facade;

import com.ruoyi.subsidy.model.bo.GbOrderBO;
import com.ruoyi.subsidy.model.bo.GbPaymentOrderBO;
import com.ruoyi.subsidy.model.param.GbOrderCreateParam;

/**
 * 国补订单领域出口。
 */
public interface IGbOrderFacade {

    /**
     * 创建待支付订单。
     *
     * @param param 创建参数
     * @return 订单信息
     */
    GbOrderBO create(GbOrderCreateParam param);

    /**
     * 查询当前会员可支付订单。
     *
     * @param memberId 会员ID
     * @param orderNo 订单号
     * @return 可支付订单
     */
    GbPaymentOrderBO getPendingPayment(Long memberId, String orderNo);

    /**
     * 处理微信支付成功通知。
     *
     * @param paymentNo 商户支付单号
     * @param transactionId 微信交易号
     * @param paidAmount 支付金额
     * @return 是否首次完成支付
     */
    boolean markWechatPaid(String paymentNo, String transactionId, java.math.BigDecimal paidAmount);

    /** 查询会员订单列表。 */
    java.util.List<GbOrderBO> listByMemberId(Long memberId);

    /** 查询会员所属订单详情。 */
    GbOrderBO getByMemberAndOrderNo(Long memberId, String orderNo);

    /** 后台人工发货。 */
    void ship(com.ruoyi.subsidy.model.param.GbShipmentParam param);

    /** 用户确认收货。 */
    void confirmReceived(Long memberId, String orderNo);

    /** 自动完成发货满七天的订单。 */
    int completeExpiredShipments();

    /** 查询后台订单列表。 */
    java.util.List<GbOrderBO> listForAdmin(String orderStatus);

    /** 会员取消待支付订单。 */
    void cancel(Long memberId, String orderNo);
}
