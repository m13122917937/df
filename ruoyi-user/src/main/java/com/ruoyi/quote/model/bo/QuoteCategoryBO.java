package com.ruoyi.quote.model.bo;

import lombok.Data;

/**
 * 报价品类业务对象。
 */
@Data
public class QuoteCategoryBO {

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
