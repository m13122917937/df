package com.ruoyi.quote.model.bo;

import lombok.Data;

/**
 * 报价价格档位业务对象。
 */
@Data
public class QuotePriceTierBO {

    /**
     * 主键
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
