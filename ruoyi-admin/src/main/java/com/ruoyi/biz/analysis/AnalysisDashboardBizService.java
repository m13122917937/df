package com.ruoyi.biz.analysis;

import com.ruoyi.analysis.facade.AnalysisDashboardFacade;
import com.ruoyi.analysis.model.bo.AnalysisDashboardBO;
import com.ruoyi.analysis.model.bo.AnalysisOrderFactBO;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 经营分析看板业务编排。
 */
@Component
public class AnalysisDashboardBizService {
    @Autowired
    private AnalysisDashboardFacade dashboardFacade;

    /**
     * 查询经营看板。
     *
     * @param query 查询条件
     * @return 看板数据
     */
    public AnalysisDashboardBO dashboard(AnalysisQuery query) {
        return dashboardFacade.dashboard(query);
    }

    /**
     * 查询绩效汇总。
     *
     * @param query 查询条件
     * @return 绩效汇总看板
     */
    public AnalysisDashboardBO performanceRollup(AnalysisQuery query) {
        return dashboardFacade.performanceRollup(query);
    }

    /**
     * 查询产渠分析。
     *
     * @param query 查询条件
     * @return 产渠分析看板
     */
    public AnalysisDashboardBO channelProduction(AnalysisQuery query) {
        return dashboardFacade.channelProduction(query);
    }

    /**
     * 查询人效分析。
     *
     * @param query 查询条件
     * @return 人效分析看板
     */
    public AnalysisDashboardBO humanEfficiency(AnalysisQuery query) {
        return dashboardFacade.humanEfficiency(query);
    }

    /**
     * 查询缺失成本的数据质量明细。
     *
     * @param query 查询条件
     * @return 未完成核算的订单商品行
     */
    public List<AnalysisOrderFactBO> dataQuality(AnalysisQuery query) {
        return dashboardFacade.dataQuality(query);
    }
}
