package com.ruoyi.quote.model.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 报价商品业务对象。
 */
@Data
public class QuoteProductBO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 品类ID
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
     * 商品编码
     */
    private String productCode;

    /**
     * 零售价
     */
    private BigDecimal retailPrice;

    /**
     * 分销1价
     */
    private BigDecimal distributor1Price;

    /**
     * 分销2价
     */
    private BigDecimal distributor2Price;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 最新一条报价
     */
    private QuotePriceHistoryBO latestQuote;
}
