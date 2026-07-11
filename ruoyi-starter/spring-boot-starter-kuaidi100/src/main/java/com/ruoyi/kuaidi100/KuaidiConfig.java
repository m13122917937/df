package com.ruoyi.kuaidi100;

import com.ruoyi.kuaidi100.properties.ExpressProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KuaidiConfig {

    @Autowired
    ExpressProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public EExpressClient expressClient() {
        return new EExpressClient(properties);
    }

}
