package com.ruoyi.subsidy.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 保存国补商品参数。
 */
@Data
@Accessors(chain = true)
public class GbProductParam {
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
