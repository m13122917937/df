package com.ruoyi.web.form.subsidy;

import lombok.Data;

import java.math.BigDecimal;

/** 国补后台 SKU 保存请求。 */
@Data
public class SubsidySkuForm {
    private Long productId;
    private String skuCode;
    private String specName;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private Integer status;
}
