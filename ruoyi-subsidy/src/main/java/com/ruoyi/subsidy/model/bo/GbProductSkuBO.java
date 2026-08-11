package com.ruoyi.subsidy.model.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 国补 SKU 业务对象。
 */
@Data
public class GbProductSkuBO {
    private Long id;
    private Long productId;
    private String skuCode;
    private String specName;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private Integer salesQuantity;
    private Integer status;
}
