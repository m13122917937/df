package com.ruoyi.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 小程序微信支付配置。
 */
@Data
@ConfigurationProperties(prefix = "wechat.pay")
public class WxPayProperties {
    private String mchId;
    private String apiV3Key;
    private String privateKeyPath;
    private String certificatePath;
    private String notifyBaseUrl;
}
