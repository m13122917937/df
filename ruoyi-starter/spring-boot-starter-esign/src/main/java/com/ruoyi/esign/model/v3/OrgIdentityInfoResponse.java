package com.ruoyi.esign.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 查询机构认证信息响应结果
 *
 * @author ruoyi
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgIdentityInfoResponse {

    private Integer realnameStatus;

    private Boolean authorizeUserInfo;

    private String orgId;

    private String orgName;

    private String orgAuthMode;

    private OrgInfo orgInfo;

    private String adminName;

    private String adminAccount;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrgInfo {
        private String orgIDCardNum;
        private String orgIDCardType;
        private String legalRepName;
        private String legalRepIDCardNum;
        private String legalRepIDCardType;
        private String corporateAccount;
        private String orgBankAccountNum;
        private String cnapsCode;
        private String authorizationDownloadUrl;
        private String licenseDownloadUrl;
        private String adminName;
        private String adminAccount;
    }
}
