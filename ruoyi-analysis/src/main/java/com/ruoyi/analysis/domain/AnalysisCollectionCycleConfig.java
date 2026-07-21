package com.ruoyi.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 回款周期配置，仅映射 ana_collection_cycle_config。 */
@Data
@TableName("ana_collection_cycle_config")
public class AnalysisCollectionCycleConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String platform;
    private String shopName;
    private Integer goodsCollectionDays;
    private Integer subsidyCollectionDays;
    private Integer nationalSubsidyCollectionDays;
    private Long createdBy;
    private LocalDateTime createdTime;
    private Long updatedBy;
    private LocalDateTime updatedTime;
}
