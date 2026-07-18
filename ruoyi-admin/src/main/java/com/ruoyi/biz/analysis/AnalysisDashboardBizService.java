package com.ruoyi.biz.analysis;

import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.model.bo.AnalysisDashboardBO;
import com.ruoyi.analysis.model.bo.AnalysisOrderFactBO;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import com.ruoyi.analysis.service.AnalysisFactService;
import com.ruoyi.analysis.service.AnalysisMetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 经营分析看板业务编排。
 */
@Component
public class AnalysisDashboardBizService {
    @Autowired
    private AnalysisMetricService metricService;
    @Autowired
    private AnalysisFactService factService;

    /**
     * 查询经营看板。
     *
     * @param query 查询条件
     * @return 看板数据
     */
    public AnalysisDashboardBO dashboard(AnalysisQuery query) {
        return metricService.dashboard(query);
    }

    /**
     * 查询订单穿透明细。
     *
     * @param query 查询条件
     * @return 订单商品行
     */
    public List<AnalysisOrderFactBO> orderDetails(AnalysisQuery query) {
        return AnalysisConvert.INSTANCE.toFactBOList(factService.listFacts(query));
    }

    /**
     * 查询缺失成本的数据质量明细。
     *
     * @param query 查询条件
     * @return 未完成核算的订单商品行
     */
    public List<AnalysisOrderFactBO> dataQuality(AnalysisQuery query) {
        query.setCalcStatus("INCOMPLETE");
        return orderDetails(query);
    }
}
