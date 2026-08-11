package com.ruoyi.web.vo.miniapp;

import lombok.Data;

import java.util.List;

/** 小程序首页聚合响应。 */
@Data
public class MiniappHomeVO {
    private List<?> banners;
    private List<MiniappCategoryVO> categories;
    private List<MiniappProductVO> recommendedProducts;
}
