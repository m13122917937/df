package com.ruoyi.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = PddProperties.WD_PREFIX)
public class PddProperties {

    final static String WD_PREFIX = "fy.pdd";


    private String clientId;


    private String clientSecret;

}
