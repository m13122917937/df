package com.ruoyi.subsidy.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

/** 国补轮播图保存参数。 */
@Data
@Accessors(chain = true)
public class GbBannerParam {
    private String bannerName;
    private String imageUrl;
    private String targetType;
    private String targetValue;
    private Integer sortOrder;
    private Integer status;
}
