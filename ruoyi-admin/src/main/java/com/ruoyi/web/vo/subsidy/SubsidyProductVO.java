package com.ruoyi.web.vo.subsidy;

import lombok.Data;

/** 国补后台商品响应。 */
@Data
public class SubsidyProductVO {
    private Long id;
    private Long categoryId;
    private String productCode;
    private String productName;
    private String subtitle;
    private String mainImageUrl;
    private Integer recommended;
    private Integer sortOrder;
    private Integer status;
}
