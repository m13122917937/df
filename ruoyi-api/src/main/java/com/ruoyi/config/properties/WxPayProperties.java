package com.ruoyi.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * 微信支付配置.
 *
 */
@Data
@ConfigurationProperties(prefix = "wechat.pay")
public class WxPayProperties implements Serializable {

    /**
     * 商户号.
     */
    private String mchId;
    /**
     * 商户密钥.
     */
    private String mchKey;

    private String mchV3Key;

    /**
     * p12证书文件的绝对路径或者以classpath:开头的类路径.
     */
    private String keyPath;

    /**
     * apiclient_key.pem证书文件的绝对路径或者以classpath:开头的类路径.
     */
    private String privateKeyPath;
    /**
     * apiclient_cert.pem证书文件的绝对路径或者以classpath:开头的类路径.
     */
    private String privateCertPath;

}
