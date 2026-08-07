package com.ruoyi.quote.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 报价商品价格明细参数。
 */
@Data
@Accessors(chain = true)
public class QuoteProductPriceParam {

    /**
     * 价格档位ID
     */
    private Long tierId;

    /**
     * 价格
     */
    private BigDecimal price;
}
