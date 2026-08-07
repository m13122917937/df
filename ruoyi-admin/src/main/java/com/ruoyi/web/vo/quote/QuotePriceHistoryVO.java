package com.ruoyi.web.vo.quote;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 报价流水响应。
 */
@Data
public class QuotePriceHistoryVO {

    /**
     * 报价日期
     */
    private LocalDate quoteDate;

    /**
     * 更新时间（精确到时分秒）
     */
    private LocalDateTime updateTime;

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
