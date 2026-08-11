package com.ruoyi.subsidy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.subsidy.domain.GbPayment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 国补支付 Mapper。
 */
@Mapper
public interface GbPaymentMapper extends BaseMapper<GbPayment> {

    /**
     * 按订单查询支付单。
     *
     * @param orderId 订单ID
     * @return 支付单
     */
    GbPayment selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 按支付单号查询支付单。
     *
     * @param paymentNo 支付单号
     * @return 支付单
     */
    GbPayment selectByPaymentNo(@Param("paymentNo") String paymentNo);

    /**
     * 仅将待支付支付单更新为已支付。
     *
     * @param paymentId 支付单ID
     * @param transactionId 微信交易号
     * @return 更新条数
     */
    int markPaidIfPending(@Param("paymentId") Long paymentId, @Param("transactionId") String transactionId);
}
