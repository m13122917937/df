package com.ruoyi.analysis;

import com.ruoyi.analysis.domain.AnalysisDailyMetric;
import com.ruoyi.analysis.model.bo.AnalysisDashboardBO;
import com.ruoyi.analysis.service.AnalysisMetricService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 经营看板各业务口径聚合回归测试。
 */
class AnalysisDashboardAggregationTest {

    @Test
    void shouldAggregatePerformanceAndChannelByDifferentDimensions() {
        AnalysisMetricService service = new AnalysisMetricService();
        List<AnalysisDailyMetric> metrics = Arrays.asList(metric("主体A", "PDD", "店铺1", "品牌A", "手机", "SKU-1", "100"),
                metric("主体A", "TB", "店铺2", "品牌A", "手机", "SKU-2", "200"));

        AnalysisDashboardBO performance = service.performanceRollup(metrics);
        AnalysisDashboardBO channel = service.channelProduction(metrics);

        assertEquals(1, performance.getRows().size());
        assertEquals(2, channel.getRows().size());
        assertEquals(new BigDecimal("300"), performance.getSummary().getSalesRevenue());
    }

    private AnalysisDailyMetric metric(String subject, String platform, String shop, String brand,
                                       String category, String goodsNo, String revenue) {
        AnalysisDailyMetric metric = new AnalysisDailyMetric();
        metric.setMetricDate(LocalDate.of(2026, 7, 18));
        metric.setSubjectName(subject);
        metric.setPlatform(platform);
        metric.setShopName(shop);
        metric.setBrand(brand);
        metric.setCategory(category);
        metric.setGoodsNo(goodsNo);
        metric.setOrderType("NORMAL");
        metric.setSalesQuantity(BigDecimal.ONE);
        metric.setGoodsIncome(new BigDecimal(revenue));
        metric.setSalesRevenue(new BigDecimal(revenue));
        metric.setGoodsCost(new BigDecimal("60"));
        metric.setGoodsGrossProfit(new BigDecimal("40"));
        metric.setFulfillmentGrossProfit(new BigDecimal("30"));
        metric.setDepartmentProfit(new BigDecimal("20"));
        metric.setOperatingProfit(new BigDecimal("10"));
        metric.setFactCount(1);
        metric.setIncompleteCount(0);
        metric.setCalcStatus("COMPLETE");
        return metric;
    }
}
