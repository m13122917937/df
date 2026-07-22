package com.ruoyi.analysis.facade.impl;

import com.ruoyi.analysis.facade.AnalysisSyncFacade;
import com.ruoyi.analysis.model.bo.AnalysisSyncBO;
import com.ruoyi.analysis.model.source.AnalysisSyncResult;
import com.ruoyi.analysis.service.AnalysisMetricService;
import com.ruoyi.analysis.service.AnalysisSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 经营分析同步领域接口实现。
 */
@Component
public class AnalysisSyncFacadeImpl implements AnalysisSyncFacade {
    @Autowired
    private AnalysisSyncService syncService;
    @Autowired
    private AnalysisMetricService metricService;

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalysisSyncBO sync(LocalDate date) {
        AnalysisSyncResult result = syncService.syncDate(date);
        for (LocalDate affectedDate : result.getAffectedDates()) {
            metricService.rebuildDate(affectedDate);
        }
        return result.getSync();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuild(LocalDate date) {
        metricService.rebuildDate(date);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncScheduled() {
        sync(LocalDate.now().minusDays(1));
    }
}
