package com.ruoyi.web.vo.quote;

import lombok.Data;

/**
 * 批发报价页品牌响应。
 */
@Data
public class QuoteBrandVO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 品牌图片
     */
    private String imageUrl;
}
