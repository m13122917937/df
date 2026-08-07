package com.ruoyi.web.vo.quote;

import lombok.Data;

/**
 * 报价价格档位保存请求。
 */
@Data
public class QuotePriceTierSaveRequest {

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
