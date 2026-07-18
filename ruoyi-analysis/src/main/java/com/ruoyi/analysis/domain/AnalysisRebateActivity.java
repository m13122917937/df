package com.ruoyi.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 返利价保活动，仅映射新表 ana_rebate_activity。
 */
@Data
@TableName("ana_rebate_activity")
public class AnalysisRebateActivity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String activityCode;
    private String activityName;
    private String brand;
    private String supplierName;
    private String scene;
    private String granularity;
    private String calculationMethod;
    private String calculationTime;
    private BigDecimal totalAmount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String attachmentData;
    private Long createdBy;
    private LocalDateTime createdTime;
    private Long updatedBy;
    private LocalDateTime updatedTime;
}
