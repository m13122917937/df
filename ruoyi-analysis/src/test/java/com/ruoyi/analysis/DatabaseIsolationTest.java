package com.ruoyi.analysis;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.domain.AnalysisDailyMetric;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.domain.AnalysisRebateActivity;
import com.ruoyi.analysis.domain.AnalysisRebateDetail;
import com.ruoyi.analysis.domain.AnalysisRefundFact;
import com.ruoyi.analysis.domain.AnalysisSyncLog;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 经营分析模块数据库隔离校验。
 */
class DatabaseIsolationTest {

    /**
     * 保证模块内所有持久化实体只能映射到 ana_* 新表。
     */
    @Test
    void persistentEntitiesMustUseAnalysisTables() {
        List<Class<?>> entities = Arrays.asList(
                AnalysisOrderFact.class,
                AnalysisRefundFact.class,
                AnalysisSyncLog.class,
                AnalysisCostConfig.class,
                AnalysisRebateActivity.class,
                AnalysisRebateDetail.class,
                AnalysisDailyMetric.class
        );

        for (Class<?> entity : entities) {
            TableName tableName = entity.getAnnotation(TableName.class);
            assertNotNull(tableName, entity.getSimpleName() + " 必须显式声明 @TableName");
            assertTrue(tableName.value().startsWith("ana_"),
                    entity.getSimpleName() + " 禁止映射既有数据库表: " + tableName.value());
        }
    }
}
