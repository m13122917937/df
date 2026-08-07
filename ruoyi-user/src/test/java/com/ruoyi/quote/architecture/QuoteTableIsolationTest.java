package com.ruoyi.quote.architecture;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.quote.domain.QuoteBrand;
import com.ruoyi.quote.domain.QuoteCategory;
import com.ruoyi.quote.domain.QuotePriceHistory;
import com.ruoyi.quote.domain.QuoteProduct;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 报价模块数据库隔离测试：持久化实体只能映射 quote_* 表。
 */
class QuoteTableIsolationTest {

    /**
     * 三个报价实体必须都指向 quote_ 前缀的表。
     */
    @Test
    void entitiesMustMapToQuoteTablesOnly() {
        assertQuoteTable(QuoteBrand.class);
        assertQuoteTable(QuoteCategory.class);
        assertQuoteTable(QuoteProduct.class);
        assertQuoteTable(QuotePriceHistory.class);
    }

    private void assertQuoteTable(final Class<?> entityClass) {
        TableName tableName = entityClass.getAnnotation(TableName.class);
        assertNotNull(tableName, entityClass.getName() + " 缺少 @TableName");
        assertTrue(tableName.value().startsWith("quote_"),
                entityClass.getName() + " 表名应为 quote_ 前缀，实际为 " + tableName.value());
    }
}
