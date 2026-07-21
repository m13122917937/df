package com.ruoyi.analysis.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 核算配置导入审计业务对象。
 */
@Data
public class AnalysisImportLogBO {
    private Long id;
    private String configType;
    private String fileName;
    private Integer overwriteFlag;
    private Integer totalCount;
    private Integer successCount;
    private Integer failedCount;
    private String status;
    private String errorMessage;
    private Long operatorId;
    private LocalDateTime createdTime;
    private LocalDateTime finishedTime;
}
