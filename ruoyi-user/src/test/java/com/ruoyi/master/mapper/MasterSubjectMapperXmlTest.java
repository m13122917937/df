package com.ruoyi.master.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 经营主体 Mapper XML 完整性测试。
 */
class MasterSubjectMapperXmlTest {

    /**
     * Mapper XML 必须存在且使用正确的 namespace。
     *
     * @throws IOException 读取资源失败
     */
    @Test
    void shouldUseMapperInterfaceAsNamespace() throws IOException {
        try (java.io.InputStream input = getClass().getClassLoader()
                .getResourceAsStream("mapper/master/MasterSubjectMapper.xml")) {
            String xml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(xml.contains("namespace=\"com.ruoyi.master.mapper.MasterSubjectMapper\""));
        }
    }
}
