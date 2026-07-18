package com.ruoyi.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 经营分析同步日志，仅映射新表 ana_sync_log。
 */
@Data
@TableName("ana_sync_log")
public class AnalysisSyncLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String syncType;
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private String status;
    private Integer readCount;
    private Integer insertCount;
    private Integer updateCount;
    private Integer skipCount;
    private String errorMessage;
    private LocalDateTime startedTime;
    private LocalDateTime finishedTime;
}
