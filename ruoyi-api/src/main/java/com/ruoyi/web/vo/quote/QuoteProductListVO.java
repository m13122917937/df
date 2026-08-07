package com.ruoyi.web.vo.quote;

import lombok.Data;

import java.util.List;

/**
 * 批发报价页商品列表响应。
 */
@Data
public class QuoteProductListVO {

    /**
     * 主键
     */
    private Long id;

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
     * 各档位价格明细
     */
    private List<QuotePriceItemVO> prices;
}
