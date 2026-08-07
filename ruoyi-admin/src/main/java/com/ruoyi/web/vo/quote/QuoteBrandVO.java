package com.ruoyi.web.vo.quote;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报价品牌响应。
 */
@Data
public class QuoteBrandVO {

    /**
     * 主键
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

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
