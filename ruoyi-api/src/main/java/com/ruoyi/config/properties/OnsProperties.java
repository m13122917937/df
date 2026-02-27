package com.ruoyi.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Rocket MQ 配置类
 *
 * @author zr 2024/3/1
 */
@Configuration
@ConfigurationProperties(prefix = "aliyun.rocketmq")
@Data
public class OnsProperties {


    private String instanceId;

    private String nameServer;

    private String accessKey;

    private String secretKey;

    private String customerGroup;

    private String orderTopic;

    private String expressTopic;



}

