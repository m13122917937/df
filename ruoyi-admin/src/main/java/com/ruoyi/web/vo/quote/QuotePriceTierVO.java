package com.ruoyi.web.vo.quote;

import lombok.Data;

/**
 * 报价价格档位响应。
 */
@Data
public class QuotePriceTierVO {

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
