package com.ruoyi.quote.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 报价品牌参数。
 */
@Data
@Accessors(chain = true)
public class QuoteBrandParam {

    /**
     * 主键（为空表示新增）
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

    /**
     * 排序
     */
    private Integer sortOrder;
}
