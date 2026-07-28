package com.ruoyi.web.vo.analysis;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 平台服务费率配置响应。
 */
@Data
public class AnalysisPlatformFeeRateVO {
    private Long id;
    private String platform;
    private Integer businessType;
    private String category;
    private BigDecimal feeRate;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedTime;
}
