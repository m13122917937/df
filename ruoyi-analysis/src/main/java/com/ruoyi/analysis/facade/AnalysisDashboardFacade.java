package com.ruoyi.analysis.facade;

import com.ruoyi.analysis.model.bo.AnalysisDashboardBO;
import com.ruoyi.analysis.model.bo.AnalysisOrderFactBO;
import com.ruoyi.analysis.model.query.AnalysisQuery;

import java.util.List;

/**
 * 经营看板领域对外接口。
 */
public interface AnalysisDashboardFacade {

    /**
     * 查询经营看板。
     *
     * @param query 查询条件
     * @return 看板数据
     */
    AnalysisDashboardBO dashboard(AnalysisQuery query);

    /**
     * 查询订单穿透明细。
     *
     * @param query 查询条件
     * @return 订单事实业务对象
     */
    List<AnalysisOrderFactBO> orderDetails(AnalysisQuery query);

    /**
     * 查询数据质量异常明细。
     *
     * @param query 查询条件
     * @return 未完成核算的订单事实
     */
    List<AnalysisOrderFactBO> dataQuality(AnalysisQuery query);
}
