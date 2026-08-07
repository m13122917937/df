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
     * 商品名
     */
    private String productName;

    /**
     * 规格/型号
     */
    private String specName;

    /**
     * 当前客户层级对应价格
     */
    private BigDecimal currentPrice;
}
