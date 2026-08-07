package com.ruoyi.quote.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 报价商品参数。
 */
@Data
@Accessors(chain = true)
public class QuoteProductParam {

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
}
