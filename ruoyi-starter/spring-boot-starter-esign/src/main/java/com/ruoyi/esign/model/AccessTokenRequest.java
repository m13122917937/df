package com.ruoyi.esign.model;

import lombok.Data;

/**
 * 获取AccessToken请求参数
 *
 * @author ruoyi
 */
@Data
public class AccessTokenRequest {

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用密钥
     */
    private String appSecret;

    /**
     * 授权类型，固定值：client_credentials
     */
    private String grantType = "client_credentials";
}
