package com.ruoyi.web.vo.quote;

import lombok.Data;

/**
 * 批发报价页商品列表查询请求。
 */
@Data
public class QuoteProductListRequest {

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
