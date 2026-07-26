package com.ruoyi.order.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 定向推送供应商 Mapper XML 完整性测试。
 */
class OrderSupplierPushMapperXmlTest {

    @Test
    void shouldProvideOrderLockStatement() throws IOException {
        String content = readMapper("mapper/order/OrderMapper.xml");
        assertTrue(content.contains("namespace=\"com.ruoyi.order.mapper.OrderMapper\""));
        assertTrue(content.contains("id=\"selectByOrderCodesForUpdate\""));
        assertTrue(content.contains("for update"));
    }

    @Test
    void shouldProvideActiveTradeLockStatement() throws IOException {
        String content = readMapper("mapper/order/TradeOrderMapper.xml");
        assertTrue(content.contains("namespace=\"com.ruoyi.order.mapper.TradeOrderMapper\""));
        assertTrue(content.contains("id=\"selectActiveByOrderCodesForUpdate\""));
        assertTrue(content.contains("for update"));
    }

    @Test
    void shouldProvideHangingOrderExpiryStatement() throws IOException {
        String content = readMapper("mapper/order/HangingOrderMapper.xml");
        assertTrue(content.contains("namespace=\"com.ruoyi.order.mapper.HangingOrderMapper\""));
        assertTrue(content.contains("id=\"expireActiveByOrderCode\""));
        assertTrue(content.contains("update_time = #{updateTime}"));
    }

    private String readMapper(String path) throws IOException {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            assertNotNull(input, "Mapper XML 不存在：" + path);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
