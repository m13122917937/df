package com.ruoyi.web.vo.analysis;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 保证金配置保存请求。
 */
@Data
public class AnalysisMarginConfigSaveRequest {
    private Long id;
    private String platform;
    private String shopName;
    private BigDecimal marginAmount;
}
