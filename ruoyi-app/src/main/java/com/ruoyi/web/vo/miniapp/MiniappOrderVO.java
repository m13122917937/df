package com.ruoyi.web.vo.miniapp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 小程序订单响应。
 */
@Data
public class MiniappOrderVO {
    private String orderNo;
    private String orderStatus;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private Date expireTime;
}
