package com.ruoyi.web.vo.miniapp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 小程序 SKU 响应。
 */
@Data
public class MiniappSkuVO {
    private Long id;
    private String specName;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
}
