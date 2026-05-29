package com.ruoyi.jky;

import com.ruoyi.jky.properties.JkyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JkyConfig {

    @Autowired
    JkyProperties properties;

    /**
     * 创建 JKY 模板实例。
     */
    @Bean
    @ConditionalOnMissingBean
    public JkyTemplate jkyTemplate() {
        return new JkyTemplate(properties);
    }

}
