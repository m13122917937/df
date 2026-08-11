package com.ruoyi.subsidy.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 保存国补 SKU 参数。
 */
@Data
@Accessors(chain = true)
public class GbProductSkuParam {
    private Long productId;
    private String skuCode;
    private String specName;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private Integer status;
}
