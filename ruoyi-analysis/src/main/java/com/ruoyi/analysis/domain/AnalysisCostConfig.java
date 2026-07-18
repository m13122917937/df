package com.ruoyi.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 经营分析核算配置，仅映射新表 ana_cost_config。
 */
@Data
@TableName("ana_cost_config")
public class AnalysisCostConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String configType;
    private LocalDate businessDate;
    private String monthValue;
    private String platform;
    private String shopName;
    private String originalOrderNo;
    private String goodsNo;
    private String goodsName;
    private String brand;
    private String category;
    private BigDecimal amount;
    private BigDecimal coefficient;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String extraData;
    private Long createdBy;
    private LocalDateTime createdTime;
    private Long updatedBy;
    private LocalDateTime updatedTime;
}
