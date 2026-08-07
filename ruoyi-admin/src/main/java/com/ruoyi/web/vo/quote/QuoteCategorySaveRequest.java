package com.ruoyi.web.vo.quote;

import lombok.Data;

/**
 * 报价品类保存请求。
 */
@Data
public class QuoteCategorySaveRequest {

    /**
     * 主键（为空表示新增）
     */
    private Long id;

    /**
     * 品类名称
     */
    private String categoryName;

    /**
     * 排序
     */
    private Integer sortOrder;
}
