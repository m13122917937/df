package com.ruoyi.esign.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * V3 获取机构认证&授权页面链接 请求对象
 *
 * @author ruoyi
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgAuthUrlRequest {

    private OrgAuthConfig orgAuthConfig;

    private AuthorizeConfig authorizeConfig;

    private RedirectConfig redirectConfig;

    private String clientType;

    private String notifyUrl;

    private Boolean transactorUseSeal;

    private Boolean orgIdentityVerify;

    private String appScheme;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrgAuthConfig {
        private String orgName;
        private String orgId;
        private OrgInfo orgInfo;
        private OrgAuthPageConfig orgAuthPageConfig;
        private TransactorInfo transactorInfo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrgInfo {
        private String orgIDCardNum;
        private String orgIDCardType;
        private String legalRepName;
        private String legalRepIDCardNum;
        private String legalRepIDCardType;
        private String orgBankAccountNum;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrgAuthPageConfig {
        private String orgDefaultAuthMode;
        private List<String> orgAvailableAuthModes;
        private List<String> orgEditableFields;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TransactorInfo {
        private String psnId;
        private String psnAccount;
        private PsnInfo psnInfo;
        private Boolean psnIdentityVerify;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PsnInfo {
        private String psnName;
        private String psnMobile;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AuthorizeConfig {
        private List<String> authorizedScopes;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RedirectConfig {
        private String redirectUrl;
        private String redirectDelayTime;
    }
}
