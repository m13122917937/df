package com.ruoyi.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 平台服务费率配置，仅映射 ana_platform_fee_rate。
 */
@Data
@TableName("ana_platform_fee_rate")
public class AnalysisPlatformFeeRate {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String platform;
    private Integer businessType;
    private String category;
    private BigDecimal feeRate;
    private String remark;
    private Long createdBy;
    private LocalDateTime createdTime;
    private Long updatedBy;
    private LocalDateTime updatedTime;
}
