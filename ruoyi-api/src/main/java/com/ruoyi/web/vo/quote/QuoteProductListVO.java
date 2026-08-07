package com.ruoyi.web.vo.quote;

import lombok.Data;

import java.math.BigDecimal;

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
}
