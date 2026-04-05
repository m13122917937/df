package com.ruoyi.esign.model;

import lombok.Data;

/**
 * 个人认证URL构建请求
 *
 * @author ruoyi
 */
@Data
public class PersonalAuthRequest {

    /**
     * 客户端回调地址，授权后的回调
     */
    private String redirectUri;

    /**
     * 第三方平台用户唯一标识
     */
    private String thirdPartyUserId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 是否需要进行人脸识别认证
     * true - 需要，false - 不需要
     */
    private Boolean faceRecognition = true;

    /**
     * 认证过期时间，单位秒，最长为24小时
     */
    private Integer expireTime = 86400;

    /**
     * 自定义参数，认证完成后会原样返回
     */
    private String state;
}
