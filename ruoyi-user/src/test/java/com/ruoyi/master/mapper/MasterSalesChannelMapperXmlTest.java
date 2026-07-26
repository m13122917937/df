package com.ruoyi.master.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 销售渠道 Mapper XML 完整性测试。
 */
class MasterSalesChannelMapperXmlTest {

    /**
     * Mapper XML 必须存在且使用正确的 namespace。
     *
     * @throws IOException 读取资源失败
     */
    @Test
    void shouldUseMapperInterfaceAsNamespace() throws IOException {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("mapper/master/MasterSalesChannelMapper.xml")) {
            assertNotNull(input);
            String xml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(xml.contains("namespace=\"com.ruoyi.master.mapper.MasterSalesChannelMapper\""));
        }
    }
}
