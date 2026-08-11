package com.ruoyi.subsidy.model.bo;

import lombok.Data;

import java.math.BigDecimal;

/** 国补退款申请结果。 */
@Data
public class GbRefundBO {
    private String refundNo;
    private String orderNo;
    private BigDecimal amount;
    private String refundStatus;
}
