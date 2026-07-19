package com.ruoyi.analysis.model.source;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * 同步日志持久化前的数据快照。
 */
@Value
@Builder
public class AnalysisSyncLogSource {
    Long id;
    String syncType;
    LocalDateTime windowStart;
    LocalDateTime windowEnd;
    String status;
    Integer readCount;
    Integer insertCount;
    Integer updateCount;
    Integer skipCount;
    String errorMessage;
    LocalDateTime startedTime;
    LocalDateTime finishedTime;
}
