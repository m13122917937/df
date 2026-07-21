package com.ruoyi.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 核算配置导入审计记录，仅映射新表 ana_import_log。
 */
@Data
@TableName("ana_import_log")
public class AnalysisImportLog {
    @TableId(type = IdType.AUTO)
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
