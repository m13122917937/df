package com.ruoyi.subsidy.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 保存国补分类参数。
 */
@Data
@Accessors(chain = true)
public class GbCategoryParam {
    private Long parentId;
    private String categoryName;
    private String iconUrl;
    private BigDecimal discountRate;
    private BigDecimal discountCapAmount;
    private String saleProvinces;
    private Integer sortOrder;
    private Integer status;
}
