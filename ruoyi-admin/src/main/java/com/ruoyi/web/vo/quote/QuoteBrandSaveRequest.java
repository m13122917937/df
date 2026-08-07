package com.ruoyi.web.vo.quote;

import lombok.Data;

/**
 * 报价品牌保存请求。
 */
@Data
public class QuoteBrandSaveRequest {

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
