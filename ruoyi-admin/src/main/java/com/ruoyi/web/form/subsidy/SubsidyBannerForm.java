package com.ruoyi.web.form.subsidy;

import lombok.Data;

/** 国补后台轮播图保存请求。 */
@Data
public class SubsidyBannerForm {
    private String bannerName;
    private String imageUrl;
    private String targetType;
    private String targetValue;
    private Integer sortOrder;
    private Integer status;
}
