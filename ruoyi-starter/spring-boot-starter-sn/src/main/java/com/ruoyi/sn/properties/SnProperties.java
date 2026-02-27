package com.ruoyi.sn.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = SnProperties.EXPRESS_PREFIX)
public class SnProperties {

    public static final String EXPRESS_PREFIX = "fy.sn";

    private String apiKey;


}
