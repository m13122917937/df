package com.ruoyi.esign.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * e签宝配置属性
 *
 * @author ruoyi
 */
@Data
@ConfigurationProperties(prefix = "esign")
public class EsignProperties {

    /**
     * 开放平台地址
     */
    private String host = "https://open.esign.cn";

    /**
     * 开发者应用ID
     */
    private String appId;

    /**
     * 应用密钥
     */
    private String appSecret;

    /**
     * 回调地址
     */
    private String redirectUri;

    /**
     * 连接超时时间（毫秒）
     */
    private Integer connectTimeout = 10000;

    /**
     * 读取超时时间（毫秒）
     */
    private Integer readTimeout = 30000;

    /**
     * 获取AccessToken API路径
     */
    public String getAccessTokenUrl() {
        return host + "/oauth/access-token";
    }

    /**
     * 获取个人认证URL
     */
    public String getPersonalAuthUrl() {
        return host + "/oauth/authorize/personal";
    }

    /**
     * 获取机构认证URL
     */
    public String getOrganizationalAuthUrl() {
        return host + "/oauth/authorize/organization";
    }

    /**
     * 查询认证信息URL
     */
    public String getAuthInfoUrl() {
        return host + "/v1/oauth-authorization/get-auth-info";
    }

    /**
     * 创建签署流程URL
     */
    public String getCreateFlowUrl() {
        return host + "/v1/sign-flow/create";
    }

    /**
     * 添加签署文件URL
     */
    public String getAddFileUrl() {
        return host + "/v1/sign-flow/add-file";
    }

    /**
     * 添加签署人URL
     */
    public String getAddSignerUrl() {
        return host + "/v1/sign-flow/add-signer";
    }

    /**
     * 发起签署流程URL
     */
    public String getStartFlowUrl() {
        return host + "/v1/sign-flow/start";
    }

    /**
     * 查询签署流程详情URL
     */
    public String getQueryFlowUrl() {
        return host + "/v1/sign-flow/get-detail";
    }

    /**
     * 获取签署页面URL
     */
    public String getSignUrl() {
        return host + "/v1/sign-flow/get-sign-url";
    }
}
