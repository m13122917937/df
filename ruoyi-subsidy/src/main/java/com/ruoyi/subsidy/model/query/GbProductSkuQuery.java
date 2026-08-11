package com.ruoyi.subsidy.model.query;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 国补 SKU 查询条件。
 */
@Data
@Accessors(chain = true)
public class GbProductSkuQuery {
    private Long id;
    private Long productId;
    private String skuCode;
    private Integer status;
}
