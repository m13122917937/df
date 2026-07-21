package com.ruoyi.user.config;

import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 企业微信客户端装配配置。 */
@Configuration
@EnableConfigurationProperties(WechatWeComProperties.class)
public class WechatWeComConfiguration {
    /**
     * 创建企业微信 SDK 客户端。
     *
     * @param properties 企业微信配置
     * @return 企业微信客户端
     */
    @Bean
    public WxCpService wxCpService(WechatWeComProperties properties) {
        WxCpDefaultConfigImpl config = new WxCpDefaultConfigImpl();
        config.setCorpId(properties.getCorpId());
        config.setCorpSecret(properties.getContactSecret());
        config.setAgentId(properties.getAgentId());
        config.setOauth2redirectUri(properties.getLoginRedirectUri());
        WxCpServiceImpl service = new WxCpServiceImpl();
        service.setWxCpConfigStorage(config);
        return service;
    }
}
