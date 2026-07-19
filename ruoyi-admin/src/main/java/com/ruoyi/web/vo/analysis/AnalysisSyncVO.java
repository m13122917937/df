package com.ruoyi.web.vo.analysis;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 经营分析同步结果响应对象。
 */
@Data
public class AnalysisSyncVO {
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
