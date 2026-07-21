package com.ruoyi.web.vo.analysis;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 核算配置导入审计记录响应对象。
 */
@Data
public class AnalysisImportLogVO {
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
