package com.ruoyi.jky;

import com.ruoyi.jky.properties.QmJkyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JkyConfig {

    @Autowired
    QmJkyProperties qmProperties;

    /**
     * 创建 QM JKY 模板实例。
     */
    @Bean
    @ConditionalOnMissingBean
    public QmJKYTemplate qmJKYTemplate() {
        return new QmJKYTemplate(qmProperties);
    }

}
