package com.ruoyi.taobao.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 淘宝开放平台 SDK 配置
 *
 * @author ruoyi
 */
@Data
@ConfigurationProperties(prefix = "fy.taobao")
public class TaobaoProperties {

    /**
     * 是否启用淘宝 SDK 自动装配
     */
    private Boolean enabled = true;

    /**
     * 网关地址，默认线上正式环境
     */
    private String serverUrl = "https://eco.taobao.com/router/rest";

    /**
     * 应用 AppKey
     */
    private String appKey;

    /**
     * 应用 AppSecret
     */
    private String appSecret;

    /**
     * 联调期使用的默认 sessionKey（店铺授权 token）。
     * 生产环境应按店铺动态获取，不要把店铺 token 写在配置里。
     */
    private String defaultSessionKey;

    /**
     * 链接超时（毫秒）
     */
    private Integer connectTimeout = 10000;

    /**
     * 读超时（毫秒）
     */
    private Integer readTimeout = 15000;
}
