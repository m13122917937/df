package com.ruoyi.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** 后台微信支付配置，仅通过环境变量或外部配置注入。 */
@Data
@ConfigurationProperties(prefix = "wechat.pay")
public class AdminWxPayProperties {
    private String mchId;
    private String apiV3Key;
    private String privateKeyPath;
    private String certificatePath;
    private String notifyBaseUrl;
}
