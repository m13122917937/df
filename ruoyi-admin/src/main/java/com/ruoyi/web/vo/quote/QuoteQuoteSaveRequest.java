package com.ruoyi.web.vo.quote;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 报价流水保存请求（当天报价，幂等覆盖当天）。
 */
@Data
public class QuoteQuoteSaveRequest {

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
