package com.ruoyi.subsidy.model.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 可支付国补订单。
 */
@Data
public class GbPaymentOrderBO {
    private String orderNo;
    private String paymentNo;
    private Long memberId;
    private BigDecimal payAmount;
    private String orderStatus;
}
