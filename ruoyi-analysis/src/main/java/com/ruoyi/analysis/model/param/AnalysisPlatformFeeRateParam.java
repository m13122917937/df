package com.ruoyi.analysis.model.param;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 平台服务费率配置保存参数。
 */
@Data
public class AnalysisPlatformFeeRateParam {
    private Long id;
    private String platform;
    private Integer businessType;
    private String category;
    private BigDecimal feeRate;
    private String remark;
    private Long operatorId;
}
