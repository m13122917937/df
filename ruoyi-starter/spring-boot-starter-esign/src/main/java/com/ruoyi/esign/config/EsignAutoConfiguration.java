package com.ruoyi.esign.config;

import com.ruoyi.esign.api.EsignAuthApi;
import com.ruoyi.esign.api.EsignFileApi;
import com.ruoyi.esign.api.EsignSignApi;
import com.ruoyi.esign.api.EsignTemplateApi;
import com.ruoyi.esign.core.DefaultEsignAuthClient;
import com.ruoyi.esign.core.DefaultEsignFileClient;
import com.ruoyi.esign.core.DefaultEsignSignClient;
import com.ruoyi.esign.core.DefaultEsignTemplateClient;
import com.ruoyi.esign.properties.EsignProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * e签宝自动配置类
 *
 * @author ruoyi
 */
@Configuration
@EnableConfigurationProperties(EsignProperties.class)
public class EsignAutoConfiguration {

    /**
     * 配置e签宝认证客户端
     *
     * @param properties 配置属性
     * @return 认证API实例
     */
    @Bean
    @ConditionalOnMissingBean
    public EsignAuthApi esignAuthApi(EsignProperties properties) {
        return new DefaultEsignAuthClient(properties);
    }

    /**
     * 配置e签宝合同签署客户端
     *
     * @param properties 配置属性
     * @return 合同签署API实例
     */
    @Bean
    @ConditionalOnMissingBean
    public EsignSignApi esignSignApi(EsignProperties properties) {
        return new DefaultEsignSignClient(properties);
    }

    /**
     * 配置e签宝文件服务客户端
     * 使用V3版本签名方式
     *
     * @param properties 配置属性
     * @return 文件服务API实例
     */
    @Bean
    @ConditionalOnMissingBean
    public EsignFileApi esignFileApi(EsignProperties properties) {
        return new DefaultEsignFileClient(properties);
    }

    /**
     * 配置e签宝合同模板客户端
     * 使用V3版本签名方式
     *
     * @param properties 配置属性
     * @return 合同模板API实例
     */
    @Bean
    @ConditionalOnMissingBean
    public EsignTemplateApi esignTemplateApi(EsignProperties properties) {
        return new DefaultEsignTemplateClient(properties);
    }
}
