package com.ruoyi.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.ruoyi.config.properties.AdminWxPayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 后台微信退款客户端配置。 */
@Configuration
@EnableConfigurationProperties(AdminWxPayProperties.class)
@ConditionalOnProperty(prefix = "wechat.pay", name = "mch-id")
public class AdminWxPayConfiguration {
    /** 创建后台微信支付客户端。 */
    @Bean
    public WxPayService adminWxPayService(final AdminWxPayProperties properties) {
        WxPayConfig config = new WxPayConfig();
        config.setMchId(properties.getMchId());
        config.setApiV3Key(properties.getApiV3Key());
        config.setPrivateKeyPath(properties.getPrivateKeyPath());
        config.setPrivateCertPath(properties.getCertificatePath());
        WxPayService service = new WxPayServiceImpl();
        service.setConfig(config);
        return service;
    }
}
