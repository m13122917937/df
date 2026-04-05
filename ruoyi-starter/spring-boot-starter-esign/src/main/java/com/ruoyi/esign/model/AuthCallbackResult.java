package com.ruoyi.esign.model;

import lombok.Data;

/**
 * 认证回调结果
 *
 * @author ruoyi
 */
@Data
public class AuthCallbackResult {

    /**
     * 认证流程ID
     */
    private String authFlowId;

    /**
     * 认证类型 1-个人认证 2-机构认证
     */
    private Integer authType;

    /**
     * 认证状态 1-认证中 2-认证成功 3-认证失败 4-认证取消
     */
    private Integer status;

    /**
     * 自定义参数，原样返回
     */
    private String state;

    /**
     * 第三方用户ID
     */
    private String thirdPartyUserId;
}
