package com.ruoyi.web.vo.quote;

import lombok.Data;

/**
 * 报价品类响应。
 */
@Data
public class QuoteCategoryVO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 品类名称
     */
    private String categoryName;

    /**
     * 品类图片
     */
    private String imageUrl;

    /**
     * 排序
     */
    private Integer sortOrder;
}
