package com.ruoyi.quote.model.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 报价商品价格明细业务对象。
 */
@Data
public class QuoteProductPriceBO {

    /**
     * 价格档位ID
     */
    private Long tierId;

    /**
     * 价格
     */
    private BigDecimal price;
}
