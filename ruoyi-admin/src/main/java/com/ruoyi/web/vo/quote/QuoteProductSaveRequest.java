package com.ruoyi.web.vo.quote;

import lombok.Data;

/**
 * 报价商品保存请求。
 */
@Data
public class QuoteProductSaveRequest {

    /**
     * 主键（为空表示新增）
     */
    private Long id;

    /**
     * 品牌ID（必须选择）
     */
    private Long brandId;

    /**
     * 品类ID（必须选择）
     */
    private Long categoryId;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 品类
     */
    private String category;

    /**
     * 商品名
     */
    private String productName;

    /**
     * 规格/型号
     */
    private String specName;

    /**
     * 排序
     */
    private Integer sortOrder;
}
