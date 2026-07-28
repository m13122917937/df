package com.ruoyi.analysis.model.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 平台服务费率配置业务对象。
 */
@Data
public class AnalysisPlatformFeeRateBO {
    private Long id;
    private String platform;
    private Integer businessType;
    private String category;
    private BigDecimal feeRate;
    private String remark;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
