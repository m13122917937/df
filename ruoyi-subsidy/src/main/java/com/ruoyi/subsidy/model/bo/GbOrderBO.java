package com.ruoyi.subsidy.model.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 国补订单业务对象。
 */
@Data
public class GbOrderBO {
    private Long id;
    private String orderNo;
    private Long memberId;
    private String orderStatus;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private Date expireTime;
}
