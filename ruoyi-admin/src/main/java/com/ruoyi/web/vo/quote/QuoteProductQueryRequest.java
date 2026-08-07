package com.ruoyi.web.vo.quote;

import lombok.Data;

/**
 * 报价商品分页查询请求。
 */
@Data
public class QuoteProductQueryRequest {

    /**
     * 品牌（精确匹配）
     */
    private String brand;

    /**
     * 品类（精确匹配）
     */
    private String category;

    /**
     * 商品名（模糊匹配）
     */
    private String productNameLike;

    /**
     * 规格/型号（模糊匹配）
     */
    private String specNameLike;
}
