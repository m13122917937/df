package com.ruoyi.config;

import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.ruoyi.config.properties.PddProperties;
import com.ruoyi.config.properties.WxMpProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@EnableConfigurationProperties(value = {PddProperties.class})
public class PddConfiguration {

    @Bean
    public PopClient popClient(PddProperties pddProperties) {
        PopClient client = new PopHttpClient(pddProperties.getClientId(), pddProperties.getClientSecret());
        return client;

    }
}
