package com.ruoyi.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.ruoyi.config.properties.WxMiniappProperties;
import com.ruoyi.config.properties.WxPayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序客户端配置。
 */
@Configuration
@EnableConfigurationProperties({WxMiniappProperties.class, WxPayProperties.class})
public class WxMiniappConfiguration {

    /**
     * 创建微信小程序服务。
     *
     * @param properties 外部配置
     * @return 小程序服务
     */
    @Bean
    public WxMaService wxMaService(final WxMiniappProperties properties) {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(properties.getAppId());
        config.setSecret(properties.getSecret());
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        return service;
    }

    /**
     * 创建微信支付服务。
     *
     * @param properties 外部支付配置
     * @return 微信支付服务
     */
    @Bean
    public WxPayService wxPayService(final WxPayProperties properties) {
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
