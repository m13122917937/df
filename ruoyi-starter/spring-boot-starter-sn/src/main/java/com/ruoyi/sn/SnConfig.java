package com.ruoyi.sn;

import com.ruoyi.sn.properties.SnProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnConfig {


    @Autowired
    SnProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public SnQueryClient snQueryClient() {
        return new SnQueryClient(properties);
    }

}
