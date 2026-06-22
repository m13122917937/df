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
     * 通知域名（用于接收认证结果异步通知）
     */
    private String notify;

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
     * 创建个人认证流程(v2)
     */
    public String getCreatePersonalAuthUrl() {
        return host + "/v2/identity/personal/create-auth";
    }

    /**
     * 创建机构认证流程(v2)
     */
    public String getCreateOrgAuthUrl() {
        return host + "/v2/identity/organization/create-auth";
    }

    /**
     * 创建机构认证流程(v3)
     * 文档：https://open.esign.cn/doc/opendoc/auth3/kcbdu7
     */
    public String getCreateOrgAuthUrlV3() {
        return host + "/v3/org-auth-url";
    }

    /**
     * 查询企业认证授权状态(v3)
     */
    public String getGetOrgAuthInfoUrl() {
        return host + "/v3/get-organize-auth-info";
    }

    /**
     * 查询机构认证信息(v3)
     */
    public String getOrgIdentityInfoUrl() {
        return host + "/v3/organizations/identity-info";
    }

    /**
     * 查询认证信息URL(v2)
     */
    public String getAuthInfoUrl() {
        return host + "/v2/identity/get-auth-info";
    }

    /**
     * 认证跳转页面前缀
     */
    public String getAuthRedirectPrefix() {
        return "https://esign.aliyun.com/auth-identity";
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

    /**
     * 下载已签署文件及附属材料URL
     */
    public String getSignFlowFileDownloadUrl(String signFlowId) {
        return host + "/v3/sign-flow/" + signFlowId + "/file-download-url";
    }

    /**
     * 下载签署中文件URL
     */
    public String getPreviewFileDownloadUrl(String signFlowId) {
        return host + "/v3/sign-flow/" + signFlowId + "/preview-file-download-url";
    }

    /**
     * 填写模板生成文件URL
     */
    public String getCreateByDocTemplateUrl() {
        return host + "/v3/files/create-by-doc-template";
    }

    /**
     * 基于文件发起签署URL
     */
    public String getCreateByFileUrl() {
        return host + "/v3/sign-flow/create-by-file";
    }

    /**
     * 查询合同模板列表URL
     */
    public String getDocTemplatesUrl() {
        return host + "/v3/doc-templates";
    }

    /**
     * 查询合同模板中控件详情URL
     */
    public String getDocTemplateInfoUrl(String docTemplateId) {
        return host + "/v3/doc-templates/" + docTemplateId;
    }
}
