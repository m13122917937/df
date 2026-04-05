package com.ruoyi.esign.model;

import lombok.Data;

/**
 * 机构认证URL构建请求
 *
 * @author ruoyi
 */
@Data
public class OrgAuthRequest {

    /**
     * 客户端回调地址，授权后的回调
     */
    private String redirectUri;

    /**
     * 第三方平台用户唯一标识（办理人唯一标识）
     */
    private String thirdPartyUserId;

    /**
     * 办理人姓名
     */
    private String agentName;

    /**
     * 办理人身份证号
     */
    private String agentIdCard;

    /**
     * 办理人手机号码
     */
    private String agentMobile;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 机构证件类型
     * 1 - 营业执照
     * 2 - 组织机构代码证
     * 3 - 统一社会信用代码
     */
    private Integer orgCertType = 3;

    /**
     * 机构证件号
     */
    private String orgCertNo;

    /**
     * 法定代表人姓名
     */
    private String legalRepName;

    /**
     * 法定代表人身份证号
     */
    private String legalRepIdCard;

    /**
     * 是否需要法定代表人认证
     * true - 需要，false - 不需要
     */
    private Boolean legalRepAuth = false;

    /**
     * 认证过期时间，单位秒，最长为24小时
     */
    private Integer expireTime = 86400;

    /**
     * 自定义参数，认证完成后会原样返回
     */
    private String state;
}
