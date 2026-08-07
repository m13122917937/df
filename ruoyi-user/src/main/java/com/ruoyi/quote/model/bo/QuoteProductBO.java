package com.ruoyi.quote.model.bo;

import lombok.Data;

import java.util.List;

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

    /**
     * 各档位价格明细
     */
    private List<QuoteProductPriceBO> prices;
}
