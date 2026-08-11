package com.ruoyi.web.vo.subsidy;

import lombok.Data;

import java.math.BigDecimal;

/** 国补后台分类响应。 */
@Data
public class SubsidyCategoryVO {
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
