package com.ruoyi.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "fadada")
@Data
public class FadadaProperties {

    private String appId;

    private String appSecret;

    private String openCorpId;

    private String serverUrl;

    private Integer connectTimeout;

    private Integer readTimeout;

}
