package com.ruoyi.user.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 企业微信手机号二次授权会话。
 */
@Data
public class WeComPrivateAuthorizationBO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String wecomUserId;
    private String ticket;
    private String errorMessage;
}
