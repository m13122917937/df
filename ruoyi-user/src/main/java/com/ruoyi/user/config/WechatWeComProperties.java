package com.ruoyi.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** 企业微信通讯录和扫码登录配置。 */
@Data
@ConfigurationProperties(prefix = "wechat.wecom")
public class WechatWeComProperties {
    private String corpId;
    private String contactSecret;
    private Integer agentId;
    private String loginRedirectUri;
    private String loginPageUri;
}
