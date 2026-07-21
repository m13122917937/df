package com.ruoyi.analysis.model.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 保证金配置业务对象。
 */
@Data
public class AnalysisMarginConfigBO {
    private Long id;
    private String platform;
    private String shopName;
    private BigDecimal marginAmount;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
