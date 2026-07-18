package com.ruoyi.web.controller.analysis;

import com.ruoyi.analysis.model.query.AnalysisQuery;
import com.ruoyi.biz.analysis.AnalysisDashboardBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 经营分析数据看板接口。
 */
@RestController
@RequestMapping("/analysis/dashboard")
public class AnalysisDashboardController extends BaseController {
    @Autowired
    private AnalysisDashboardBizService dashboardBizService;

    /**
     * 经营统计。
     */
    @GetMapping("/operationStats")
    public AjaxResult operationStats(AnalysisQuery query) {
        return AjaxResult.success(dashboardBizService.dashboard(query));
    }

    /**
     * 绩效汇总。
     */
    @GetMapping("/performanceRollup")
    public AjaxResult performanceRollup(AnalysisQuery query) {
        return AjaxResult.success(dashboardBizService.dashboard(query));
    }

    /**
     * 产渠分析。
     */
    @GetMapping("/channelProduction")
    public AjaxResult channelProduction(AnalysisQuery query) {
        return AjaxResult.success(dashboardBizService.dashboard(query));
    }

    /**
     * 人效分析。
     */
    @GetMapping("/humanEfficiency")
    public AjaxResult humanEfficiency(AnalysisQuery query) {
        return AjaxResult.success(dashboardBizService.dashboard(query));
    }

    /**
     * 指标树。
     */
    @GetMapping("/metricTree")
    public AjaxResult metricTree(AnalysisQuery query) {
        return AjaxResult.success(dashboardBizService.dashboard(query).getMetricTree());
    }

    /**
     * 订单穿透明细。
     */
    @GetMapping("/orderDetails")
    public AjaxResult orderDetails(AnalysisQuery query) {
        return AjaxResult.success(dashboardBizService.orderDetails(query));
    }

    /**
     * 数据质量明细。
     */
    @GetMapping("/dataQuality")
    public AjaxResult dataQuality(AnalysisQuery query) {
        return AjaxResult.success(dashboardBizService.dataQuality(query));
    }
}
