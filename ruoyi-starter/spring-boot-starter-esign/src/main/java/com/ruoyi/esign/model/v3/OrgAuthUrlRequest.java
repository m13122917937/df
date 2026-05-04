package com.ruoyi.esign.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * V3 获取机构认证&授权页面链接 请求对象
 * 文档：https://open.esign.cn/doc/opendoc/auth3/kcbdu7
 *
 * @author ruoyi
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgAuthUrlRequest {

    /**
     * accessToken
     */
    private String accessToken;

    /**
     * 第三方平台用户唯一标识（办理人在你方平台的唯一标识）
     */
    private String thirdPartyUserId;

    /**
     * 机构在第三方平台的唯一标识
     */
    private String orgThirdUniqueId;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 机构证件类型
     * <ul>
     * <li>CREDIT_CODE - 统一社会信用代码</li>
     * <li>BUSINESS_LICENSE - 营业执照</li>
     * <li>ORG_CODE - 组织机构代码证</li>
     * </ul>
     */
    private String orgCardType;

    /**
     * 机构证件号码
     */
    private String orgCardNo;

    /**
     * 是否自动完成认证
     * true - 自动进入认证
     * false - 需要点击按钮开始认证
     */
    private Boolean authorizeAuto;

    /**
     * 认证上下文
     */
    private Context context;

    /**
     * 经办人银行账号
     */
    private String agentAccountNum;

    /**
     * 经办人姓名
     */
    private String agentName;

    /**
     * 经办人身份证号
     */
    private String agentIdCard;

    /**
     * 经办人手机号码
     */
    private String agentMobile;

    /**
     * 认证结果异步通知地址
     */
    private String notifyUrl;

    /**
     * 认证完成后跳转地址
     */
    private String redirectUrl;

    /**
     * 业务流水号
     */
    private String serialNo;

    /**
     * 认证链接有效期，单位分钟，默认120，范围 1-1440
     */
    private Integer expire;

    /**
     * 机构认证配置项
     */
    private OrgAuthConfig orgAuthConfig;

    /**
     * 认证上下文
     */
    @Data
    public static class Context {
        /**
         * 法定代表人姓名
         */
        private String legalRepName;

        /**
         * 法定代表人身份证号
         */
        private String legalRepIdCard;
    }

    /**
     * 机构认证配置项
     */
    @Data
    public static class OrgAuthConfig {
        /**
         * 认证完成后是否自动跳转回发起方
         * true - 自动跳转，false - 需要用户手动点击跳转
         */
        private Boolean autoJump;
    }
}
