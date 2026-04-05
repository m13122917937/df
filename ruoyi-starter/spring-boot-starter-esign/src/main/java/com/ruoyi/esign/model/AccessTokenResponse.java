package com.ruoyi.esign.model;

import lombok.Data;

/**
 * 获取AccessToken响应
 *
 * @author ruoyi
 */
@Data
public class AccessTokenResponse {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 过期时间，单位秒
     */
    private Integer expiresIn;

    /**
     * 令牌类型
     */
    private String tokenType;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误信息
     */
    private String message;
}
