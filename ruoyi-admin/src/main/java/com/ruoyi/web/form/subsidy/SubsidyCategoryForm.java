package com.ruoyi.web.form.subsidy;

import lombok.Data;

import java.math.BigDecimal;

/** 国补后台分类保存请求。 */
@Data
public class SubsidyCategoryForm {
    private Long parentId;
    private String categoryName;
    private String iconUrl;
    private BigDecimal discountRate;
    private BigDecimal discountCapAmount;
    private String saleProvinces;
    private Integer sortOrder;
    private Integer status;
}
