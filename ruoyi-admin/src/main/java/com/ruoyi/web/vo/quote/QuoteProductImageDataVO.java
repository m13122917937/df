package com.ruoyi.web.vo.quote;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 报价单图片数据响应（后台，含三档价格）。
 */
@Data
public class QuoteProductImageDataVO {

    /**
     * 品牌
     */
    private String brand;

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
     * 批发1价
     */
    private BigDecimal distributor1Price;

    /**
     * 批发2价
     */
    private BigDecimal distributor2Price;
}
