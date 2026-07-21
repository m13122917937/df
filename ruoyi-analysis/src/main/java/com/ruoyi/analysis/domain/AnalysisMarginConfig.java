package com.ruoyi.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 保证金配置，仅映射 ana_margin_config。
 */
@Data
@TableName("ana_margin_config")
public class AnalysisMarginConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String platform;
    private String shopName;
    private BigDecimal marginAmount;
    private Long createdBy;
    private LocalDateTime createdTime;
    private Long updatedBy;
    private LocalDateTime updatedTime;
}
