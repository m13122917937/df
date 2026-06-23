package com.ruoyi.jky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 启明吉客云配置属性。
 */
@Data
@Configuration
@ConfigurationProperties(prefix = QmJkyProperties.PREFIX)
public class QmJkyProperties extends JkyProperties {

    public static final String PREFIX = "fy.jky.qm";

}