package com.ruoyi.quote.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 报价模块 Mapper XML 完整性测试。
 */
class QuoteMapperXmlTest {

    /**
     * 价格档位 Mapper XML 必须存在且使用正确的 namespace。
     *
     * @throws IOException 读取资源失败
     */
    @Test
    void quotePriceTierMapperXmlShouldUseInterfaceAsNamespace() throws IOException {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("mapper/quote/QuotePriceTierMapper.xml")) {
            assertNotNull(input);
            String xml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(xml.contains("namespace=\"com.ruoyi.quote.mapper.QuotePriceTierMapper\""));
        }
    }

    /**
     * 报价商品 Mapper XML 必须存在且使用正确的 namespace。
     *
     * @throws IOException 读取资源失败
     */
    @Test
    void quoteProductMapperXmlShouldUseInterfaceAsNamespace() throws IOException {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("mapper/quote/QuoteProductMapper.xml")) {
            assertNotNull(input);
            String xml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(xml.contains("namespace=\"com.ruoyi.quote.mapper.QuoteProductMapper\""));
        }
    }

    /**
     * 价格明细 Mapper XML 必须存在且使用正确的 namespace。
     *
     * @throws IOException 读取资源失败
     */
    @Test
    void quoteProductPriceMapperXmlShouldUseInterfaceAsNamespace() throws IOException {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("mapper/quote/QuoteProductPriceMapper.xml")) {
            assertNotNull(input);
            String xml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(xml.contains("namespace=\"com.ruoyi.quote.mapper.QuoteProductPriceMapper\""));
        }
    }
}
