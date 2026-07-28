package com.ruoyi.web.vo.analysis;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 平台服务费率保存请求。
 */
@Data
public class AnalysisPlatformFeeRateSaveRequest {
    private Long id;
    private String platform;
    private Integer businessType;
    private String category;
    private BigDecimal feeRate;
    private String remark;
}
