package com.ruoyi.taobao.config;

import com.ruoyi.taobao.properties.TaobaoProperties;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 淘宝开放平台 SDK 自动装配
 *
 * @author ruoyi
 */
@Configuration
@EnableConfigurationProperties(TaobaoProperties.class)
@ConditionalOnProperty(prefix = "fy.taobao", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TaobaoAutoConfiguration {

    /**
     * 注册全局 TaobaoClient 单例
     *
     * @param properties 淘宝开放平台配置
     * @return TaobaoClient 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public TaobaoClient taobaoClient(final TaobaoProperties properties) {
        DefaultTaobaoClient client = new DefaultTaobaoClient(
                properties.getServerUrl(),
                properties.getAppKey(),
                properties.getAppSecret());
        client.setConnectTimeout(properties.getConnectTimeout());
        client.setReadTimeout(properties.getReadTimeout());
        return client;
    }
}
