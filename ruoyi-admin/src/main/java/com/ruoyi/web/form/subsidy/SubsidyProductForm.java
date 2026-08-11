package com.ruoyi.web.form.subsidy;

import lombok.Data;

/** 国补后台商品保存请求。 */
@Data
public class SubsidyProductForm {
    private Long categoryId;
    private String productCode;
    private String productName;
    private String subtitle;
    private String mainImageUrl;
    private String detailContent;
    private Integer recommended;
    private Integer sortOrder;
    private Integer status;
}
