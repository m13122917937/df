package com.ruoyi.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信小程序配置。
 */
@Data
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WxMiniappProperties {
    private String appId;
    private String secret;
}
