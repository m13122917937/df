package com.ruoyi.kuaidi100;

import com.ruoyi.kuaidi100.factory.DesensitizedOrderStrategyFactory;
import com.ruoyi.kuaidi100.properties.ExpressProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KuaidiConfig {

    @Autowired
    ExpressProperties properties;

    @Autowired
    private DesensitizedOrderStrategyFactory strategyFactory;

    @Bean
    @ConditionalOnMissingBean // 如果用户没有自己定义，则创建默认 Bean
    public EExpressClient expressClient() {
        EExpressClient client = new EExpressClient(properties);
        client.setStrategyFactory(strategyFactory);
        return client;
    }


}
