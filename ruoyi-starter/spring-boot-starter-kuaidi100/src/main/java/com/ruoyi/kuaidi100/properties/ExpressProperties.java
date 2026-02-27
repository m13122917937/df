package com.ruoyi.kuaidi100.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 快递信息查询条件.
 *
 */
@Data
@Configuration
@ConfigurationProperties(prefix = ExpressProperties.EXPRESS_PREFIX)
public class ExpressProperties {

    public static final String EXPRESS_PREFIX = "fy.express";


    private String subscribeUrl;

    private String key;

    private String secret;

    private String customer;
}
