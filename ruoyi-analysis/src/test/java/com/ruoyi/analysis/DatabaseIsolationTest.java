package com.ruoyi.analysis;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.domain.AnalysisDailyMetric;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.domain.AnalysisRefundFact;
import com.ruoyi.analysis.domain.AnalysisSyncLog;
import com.ruoyi.analysis.mapper.AnalysisCostConfigMapper;
import com.ruoyi.analysis.mapper.AnalysisDailyMetricMapper;
import com.ruoyi.analysis.mapper.AnalysisOrderFactMapper;
import com.ruoyi.analysis.mapper.AnalysisRefundFactMapper;
import com.ruoyi.analysis.mapper.AnalysisSyncLogMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
                AnalysisDailyMetric.class
        );

        for (Class<?> entity : entities) {
            TableName tableName = entity.getAnnotation(TableName.class);
            assertNotNull(tableName, entity.getSimpleName() + " 必须显式声明 @TableName");
            assertTrue(tableName.value().startsWith("ana_"),
                    entity.getSimpleName() + " 禁止映射既有数据库表: " + tableName.value());
        }
    }

    /**
     * 保证每个分析 Mapper 都有独立 XML，并且 namespace 与接口一致。
     *
     * @throws IOException XML 读取失败
     */
    @Test
    void everyMapperMustHaveXmlMapping() throws IOException {
        List<Class<?>> mappers = Arrays.asList(
                AnalysisOrderFactMapper.class, AnalysisRefundFactMapper.class,
                AnalysisSyncLogMapper.class, AnalysisCostConfigMapper.class,
                AnalysisDailyMetricMapper.class
        );
        for (Class<?> mapper : mappers) {
            assertMapperXml(mapper);
        }
    }

    private void assertMapperXml(Class<?> mapper) throws IOException {
        String resource = "mapper/analysis/" + mapper.getSimpleName() + ".xml";
        try (InputStream input = mapper.getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(input, mapper.getSimpleName() + " 缺少 Mapper XML");
            String xml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(xml.contains("namespace=\"" + mapper.getName() + "\""),
                    mapper.getSimpleName() + " 的 namespace 不正确");
        }
    }
}
