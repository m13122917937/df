package com.ruoyi.analysis;

import com.ruoyi.analysis.convert.AnalysisMetricConvert;
import com.ruoyi.analysis.convert.AnalysisSyncConvert;
import com.ruoyi.analysis.domain.AnalysisDailyMetric;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.model.bo.AnalysisDashboardBO;
import com.ruoyi.analysis.model.source.AnalysisMetricCalculation;
import com.ruoyi.analysis.model.source.AnalysisOrderFactSource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 经营分析模块 MapStruct 转换链校验。
 */
class AnalysisMapStructConvertTest {

    /**
     * 验证同步快照能够完整转换为订单事实实体。
     */
    @Test
    void shouldConvertOrderSourceToFact() {
        AnalysisOrderFactSource source = AnalysisOrderFactSource.builder()
                .jkyTradeNo("JKY-1").goodsLineKey("LINE-1").goodsName("测试商品")
                .businessDate(LocalDate.of(2026, 7, 18)).quantity(BigDecimal.ONE)
                .build();

        AnalysisOrderFact fact = AnalysisSyncConvert.INSTANCE.toDomain(source);

        assertEquals("JKY-1", fact.getJkyTradeNo());
        assertEquals("LINE-1", fact.getGoodsLineKey());
        assertEquals("测试商品", fact.getGoodsName());
        assertEquals(BigDecimal.ONE, fact.getQuantity());
    }

    /**
     * 验证计算快照可以转换为持久化实体和看板对象。
     */
    @Test
    void shouldConvertCalculationToMetricAndDashboard() {
        AnalysisMetricCalculation source = AnalysisMetricCalculation.builder()
                .metricDate(LocalDate.of(2026, 7, 18)).platform("PDD")
                .salesRevenue(new BigDecimal("200")).goodsGrossProfit(new BigDecimal("40"))
                .directHeadcount(new BigDecimal("3"))
                .factCount(2).incompleteCount(0).calcStatus("COMPLETE").build();

        AnalysisDailyMetric metric = AnalysisMetricConvert.INSTANCE.toDomain(source);
        AnalysisDashboardBO.MetricBO summary = AnalysisMetricConvert.INSTANCE.toMetricBO(source);

        assertEquals("PDD", metric.getPlatform());
        assertEquals(new BigDecimal("200"), metric.getSalesRevenue());
        assertEquals(new BigDecimal("20.00"), summary.getGoodsGrossMargin());
        assertEquals(new BigDecimal("3"), metric.getDirectHeadcount());
        assertEquals("COMPLETE", summary.getCalcStatus());
    }

}
