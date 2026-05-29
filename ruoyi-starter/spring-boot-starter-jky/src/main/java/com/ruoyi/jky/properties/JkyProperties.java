package com.ruoyi.jky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = JkyProperties.PREFIX)
public class JkyProperties {

    public static final String PREFIX = "fy.jky";

    private String appKey;

    private String appSecret;

    private String baseUrl = "https://open.jackyun.com/open/openapi/do";

    private String token;

    private String version = "v1.0";

    private String contentType = "json";

    private Integer connectTimeout = 10000;

}
