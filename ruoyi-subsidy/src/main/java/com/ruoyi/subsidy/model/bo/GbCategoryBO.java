package com.ruoyi.subsidy.model.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 国补分类业务对象。
 */
@Data
public class GbCategoryBO {
    private Long id;
    private Long parentId;
    private String categoryName;
    private String iconUrl;
    private BigDecimal discountRate;
    private BigDecimal discountCapAmount;
    private String saleProvinces;
    private Integer sortOrder;
    private Integer status;
}
