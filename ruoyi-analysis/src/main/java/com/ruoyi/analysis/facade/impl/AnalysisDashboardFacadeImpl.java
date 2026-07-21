package com.ruoyi.analysis.facade.impl;

import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.convert.AnalysisQueryConvert;
import com.ruoyi.analysis.domain.AnalysisDailyMetric;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.facade.AnalysisDashboardFacade;
import com.ruoyi.analysis.model.bo.AnalysisDashboardBO;
import com.ruoyi.analysis.model.bo.AnalysisOrderFactBO;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import com.ruoyi.analysis.model.query.AnalysisFactQuery;
import com.ruoyi.analysis.service.AnalysisFactService;
import com.ruoyi.analysis.service.AnalysisMetricService;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.framework.mybatis.DynamicCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 经营看板领域接口实现。
 */
@Component
public class AnalysisDashboardFacadeImpl implements AnalysisDashboardFacade {
    @Autowired
    private AnalysisMetricService metricService;
    @Autowired
    private AnalysisFactService factService;

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalysisDashboardBO dashboard(AnalysisQuery query) {
        return metricService.dashboard(listMetrics(query));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalysisDashboardBO performanceRollup(AnalysisQuery query) {
        return metricService.performanceRollup(listMetrics(query));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalysisDashboardBO channelProduction(AnalysisQuery query) {
        return metricService.channelProduction(listMetrics(query));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalysisDashboardBO humanEfficiency(AnalysisQuery query) {
        return metricService.humanEfficiency(listMetrics(query));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AnalysisOrderFactBO> orderDetails(AnalysisQuery query) {
        return listFacts(AnalysisQueryConvert.INSTANCE.toFactQuery(query));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AnalysisOrderFactBO> dataQuality(AnalysisQuery query) {
        return listFacts(AnalysisQueryConvert.INSTANCE.toIncompleteFactQuery(query));
    }

    private List<AnalysisOrderFactBO> listFacts(AnalysisFactQuery query) {
        List<AnalysisOrderFact> facts = factService.list(DynamicCondition.toWrapper(
                query, SortBy.of("-business_date,-id")));
        return AnalysisConvert.INSTANCE.toFactBOList(facts);
    }

    private List<AnalysisDailyMetric> listMetrics(AnalysisQuery query) {
        return metricService.list(DynamicCondition.toWrapper(
                AnalysisQueryConvert.INSTANCE.toMetricQuery(query), SortBy.of("+metric_date")));
    }
}
