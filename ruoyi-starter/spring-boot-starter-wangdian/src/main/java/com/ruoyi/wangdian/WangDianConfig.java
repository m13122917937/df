package com.ruoyi.wangdian;

import com.ruoyi.wangdian.properties.WdProperties;
import com.ruoyi.wangdian.utils.WdtClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WangDianConfig {

    @Autowired
    WdProperties properties;


    @Bean
    @ConditionalOnMissingBean // 如果用户没有自己定义，则创建默认 Bean
    public WdtClient wdtClient() {
        return new WdtClient(properties);
    }

}
