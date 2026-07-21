package com.ruoyi.web.vo.analysis;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 保证金配置响应。
 */
@Data
public class AnalysisMarginConfigVO {
    private Long id;
    private String platform;
    private String shopName;
    private BigDecimal marginAmount;
    private LocalDateTime updatedTime;
}
