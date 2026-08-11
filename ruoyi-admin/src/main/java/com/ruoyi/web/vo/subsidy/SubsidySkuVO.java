package com.ruoyi.web.vo.subsidy;

import lombok.Data;

import java.math.BigDecimal;

/** 国补后台 SKU 响应。 */
@Data
public class SubsidySkuVO {
    private Long id;
    private Long productId;
    private String skuCode;
    private String specName;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private Integer salesQuantity;
    private Integer status;
}
