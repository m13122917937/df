package com.ruoyi.quote.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 报价品类参数。
 */
@Data
@Accessors(chain = true)
public class QuoteCategoryParam {

    /**
     * 主键（为空表示新增）
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
