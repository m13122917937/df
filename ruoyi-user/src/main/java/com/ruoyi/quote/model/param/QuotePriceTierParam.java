package com.ruoyi.quote.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 报价价格档位参数。
 */
@Data
@Accessors(chain = true)
public class QuotePriceTierParam {

    /**
     * 主键（为空表示新增）
     */
    private Long id;

    /**
     * 档位名称
     */
    private String tierName;

    /**
     * 排序
     */
    private Integer sortOrder;
}
