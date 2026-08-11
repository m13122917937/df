package com.ruoyi.subsidy.model.bo;

import lombok.Data;

import java.math.BigDecimal;

/** 发起微信退款所需订单数据。 */
@Data
public class GbRefundPaymentBO {
    private String refundNo;
    private String paymentNo;
    private String wechatTransactionId;
    private BigDecimal amount;
    private String reason;
}
