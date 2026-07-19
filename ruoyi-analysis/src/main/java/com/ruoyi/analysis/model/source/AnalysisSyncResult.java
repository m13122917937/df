package com.ruoyi.analysis.model.source;

import com.ruoyi.analysis.model.bo.AnalysisSyncBO;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.Set;

/**
 * 同步领域服务返回给 Facade 的内部结果。
 */
@Value
@AllArgsConstructor
public class AnalysisSyncResult {
    AnalysisSyncBO sync;
    Set<LocalDate> affectedDates;
}
