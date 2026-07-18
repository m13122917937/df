package com.ruoyi.analysis.model.bo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 经营分析同步结果业务对象。
 */
@Data
public class AnalysisSyncBO {
    private Long id;
    private String syncType;
    private LocalDate businessDate;
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
