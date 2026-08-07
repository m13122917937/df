package com.ruoyi.web.vo.quote;

import lombok.Data;

import java.time.LocalDateTime;

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
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 排序
     */
    private Integer sortOrder;
}
