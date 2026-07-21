package com.ruoyi.analysis.model.param;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 保证金配置保存参数。
 */
@Data
public class AnalysisMarginConfigParam {
    private Long id;
    private String platform;
    private String shopName;
    private BigDecimal marginAmount;
    private Long operatorId;
}
