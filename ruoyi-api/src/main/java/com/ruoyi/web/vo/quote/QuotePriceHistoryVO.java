package com.ruoyi.web.vo.quote;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 批发报价页历史报价响应。
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
     * 当前客户层级对应价格
     */
    private BigDecimal price;
}
