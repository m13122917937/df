package com.ruoyi.web.controller.analysis;

import com.ruoyi.biz.analysis.AnalysisDashboardBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.convert.analysis.AnalysisWebConvert;
import com.ruoyi.web.vo.analysis.AnalysisDashboardVO;
import com.ruoyi.web.vo.analysis.AnalysisQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 经营分析数据看板接口。
 */
@RestController
@RequestMapping("/analysis/dashboard")
@PreAuthorize("@ss.hasPermi('analysis:dashboard:list')")
public class AnalysisDashboardController extends BaseController {
    @Autowired
    private AnalysisDashboardBizService dashboardBizService;

    /**
     * 经营统计。
     */
    @GetMapping("/operationStats")
    public AjaxResult operationStats(AnalysisQueryRequest request) {
        AnalysisDashboardVO result = AnalysisWebConvert.INSTANCE.toDashboardVO(
                dashboardBizService.dashboard(AnalysisWebConvert.INSTANCE.toQuery(request)));
        return AjaxResult.success(result);
    }

    /**
     * 绩效汇总。
     */
    @GetMapping("/performanceRollup")
    public AjaxResult performanceRollup(AnalysisQueryRequest request) {
        AnalysisDashboardVO result = AnalysisWebConvert.INSTANCE.toDashboardVO(
                dashboardBizService.dashboard(AnalysisWebConvert.INSTANCE.toQuery(request)));
        return AjaxResult.success(result);
    }

    /**
     * 产渠分析。
     */
    @GetMapping("/channelProduction")
    public AjaxResult channelProduction(AnalysisQueryRequest request) {
        AnalysisDashboardVO result = AnalysisWebConvert.INSTANCE.toDashboardVO(
                dashboardBizService.dashboard(AnalysisWebConvert.INSTANCE.toQuery(request)));
        return AjaxResult.success(result);
    }

    /**
     * 人效分析。
     */
    @GetMapping("/humanEfficiency")
    public AjaxResult humanEfficiency(AnalysisQueryRequest request) {
        AnalysisDashboardVO result = AnalysisWebConvert.INSTANCE.toDashboardVO(
                dashboardBizService.dashboard(AnalysisWebConvert.INSTANCE.toQuery(request)));
        return AjaxResult.success(result);
    }

    /**
     * 指标树。
     */
    @GetMapping("/metricTree")
    public AjaxResult metricTree(AnalysisQueryRequest request) {
        AnalysisDashboardVO result = AnalysisWebConvert.INSTANCE.toDashboardVO(
                dashboardBizService.dashboard(AnalysisWebConvert.INSTANCE.toQuery(request)));
        return AjaxResult.success(result.getMetricTree());
    }

    /**
     * 订单穿透明细。
     */
    @GetMapping("/orderDetails")
    public AjaxResult orderDetails(AnalysisQueryRequest request) {
        return AjaxResult.success(AnalysisWebConvert.INSTANCE.toOrderFactVOList(
                dashboardBizService.orderDetails(AnalysisWebConvert.INSTANCE.toQuery(request))));
    }

    /**
     * 数据质量明细。
     */
    @GetMapping("/dataQuality")
    public AjaxResult dataQuality(AnalysisQueryRequest request) {
        return AjaxResult.success(AnalysisWebConvert.INSTANCE.toOrderFactVOList(
                dashboardBizService.dataQuality(AnalysisWebConvert.INSTANCE.toQuery(request))));
    }
}
