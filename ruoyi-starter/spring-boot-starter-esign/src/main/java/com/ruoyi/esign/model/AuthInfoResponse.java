package com.ruoyi.esign.model;

import lombok.Data;

/**
 * 认证信息查询响应
 *
 * @author ruoyi
 */
@Data
public class AuthInfoResponse {

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

    /**
     * 认证类型：1-个人认证 2-机构认证
     */
    private Integer authType;

    /**
     * 个人认证信息，authType=1时返回
     */
    private PersonalAuthInfo personalAuthInfo;

    /**
     * 机构认证信息，authType=2时返回
     */
    private OrgAuthInfo orgAuthInfo;
}
