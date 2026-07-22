package com.ruoyi.analysis;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.analysis.convert.AnalysisQueryConvert;
import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.domain.AnalysisDailyMetric;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import com.ruoyi.framework.mybatis.DynamicCondition;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Analysis 统一动态查询条件校验。
 */
class AnalysisDynamicConditionTest {

    /**
     * 验证不同领域实体使用各自正确的日期字段和公共筛选字段。
     */
    @Test
    void shouldBuildEntitySpecificDynamicConditions() {
        AnalysisQuery query = new AnalysisQuery();
        query.setStartDate(LocalDate.of(2026, 7, 1));
        query.setEndDate(LocalDate.of(2026, 7, 18));
        query.setPlatform("PDD");

        Wrapper<AnalysisOrderFact> factWrapper = DynamicCondition.toWrapper(
                AnalysisQueryConvert.INSTANCE.toFactQuery(query));
        Wrapper<AnalysisDailyMetric> metricWrapper = DynamicCondition.toWrapper(
                AnalysisQueryConvert.INSTANCE.toMetricQuery(query));
        Wrapper<AnalysisCostConfig> configWrapper = DynamicCondition.toWrapper(
                AnalysisQueryConvert.INSTANCE.toCostConfigQuery(query));

        assertContains(factWrapper.getSqlSegment(), "business_date", "platform");
        assertContains(metricWrapper.getSqlSegment(), "metric_date", "platform");
        assertContains(configWrapper.getSqlSegment(), "business_date", "config_type", "platform");
    }

    private void assertContains(String sql, String... fields) {
        for (String field : fields) {
            assertTrue(sql.contains(field), "动态查询缺少字段：" + field + "，SQL=" + sql);
        }
    }
}
