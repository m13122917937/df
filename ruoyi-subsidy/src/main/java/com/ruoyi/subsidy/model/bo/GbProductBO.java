package com.ruoyi.subsidy.model.bo;

import lombok.Data;

/**
 * 国补商品业务对象。
 */
@Data
public class GbProductBO {
    private Long id;
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
