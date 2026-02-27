package com.ruoyi.wangdian.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = WdProperties.WD_PREFIX)
public class WdProperties {

    final static String WD_PREFIX = "fy.wd";


    private String sid;

    private String appkey;

    private String appsecret;

    private String salt;

    private String baseUrl;


    private int connectTimeout;

    private int readTimeout;
}
