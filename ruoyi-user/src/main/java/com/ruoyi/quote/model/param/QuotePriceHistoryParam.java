package com.ruoyi.quote.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 报价流水参数。
 */
@Data
@Accessors(chain = true)
public class QuotePriceHistoryParam {

    /**
     * 报价商品ID
     */
    private Long productId;

    /**
     * 报价日期（为空表示当天）
     */
    private LocalDate quoteDate;

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
