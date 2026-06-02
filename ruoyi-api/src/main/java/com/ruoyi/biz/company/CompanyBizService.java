package com.ruoyi.biz.company;

import cn.hutool.core.lang.Assert;
import com.ruoyi.esign.api.EsignAuthApi;
import com.ruoyi.esign.model.v3.OrgAuthUrlRequest;
import com.ruoyi.esign.properties.EsignProperties;
import com.ruoyi.mapper.user.CompanyConvert;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.consts.CompanyEnum;
import com.ruoyi.user.model.param.CompanyParam;
import com.ruoyi.user.model.param.MemberCompanyParam;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.user.model.query.MemberCompanyQuery;
import com.ruoyi.web.vo.user.CompanyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CompanyBizService {

    @Autowired
    ICompanyFacade companyFacade;

    @Autowired
    EsignAuthApi esignAuthApi;

    @Autowired
    EsignProperties esignProperties;

    public CompanyVO info(Long companyId) {
        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(companyId));
        return CompanyConvert.INSTANCE.toVo(companyBO);
    }

    /**
     * 处理机构认证授权回调
     *
     * @param companyId 企业ID
     * @param userId 用户ID
     */
    public void authCallBack(Long companyId, Long userId) {
        companyFacade.update(new CompanyParam().setContractAuthStatus(CompanyEnum.ContractAuthStatus.USE.getValue()), new CompanyQuery().setId(companyId));
        companyFacade.update(new MemberCompanyParam().setContractSignAuthStatus(CompanyEnum.ContractSignAuthStatus.USE.getValue()),
                new MemberCompanyQuery().setCompanyId(companyId).setUserId(userId));
    }

    /**
     * 获取机构认证授权页面链接
     *
     * @param companyId 企业ID
     * @param userId 用户ID（办理人在你方平台的唯一标识）
     * @return 认证跳转URL
     */
    public String getOrgAuthUrl(Long companyId, Long userId) {
        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(companyId));
        Assert.notNull(companyBO, "企业不存在");

        OrgAuthUrlRequest request = new OrgAuthUrlRequest();
        OrgAuthUrlRequest.OrgAuthConfig orgAuthConfig = new OrgAuthUrlRequest.OrgAuthConfig();
        orgAuthConfig.setOrgName(companyBO.getCompanyName());

        OrgAuthUrlRequest.OrgInfo orgInfo = new OrgAuthUrlRequest.OrgInfo();
        orgInfo.setOrgIDCardNum(companyBO.getCreditCode());
        orgInfo.setOrgIDCardType("CRED_ORG_USCC");
        orgInfo.setLegalRepName(companyBO.getCorporateName());
        orgAuthConfig.setOrgInfo(orgInfo);

        OrgAuthUrlRequest.OrgAuthPageConfig orgAuthPageConfig = new OrgAuthUrlRequest.OrgAuthPageConfig();
        orgAuthPageConfig.setOrgDefaultAuthMode("ORG_BANK_TRANSFER");
        orgAuthConfig.setOrgAuthPageConfig(orgAuthPageConfig);
        request.setOrgAuthConfig(orgAuthConfig);

        OrgAuthUrlRequest.AuthorizeConfig authorizeConfig = new OrgAuthUrlRequest.AuthorizeConfig();
        authorizeConfig.setAuthorizedScopes(List.of("org_initiate_sign", "psn_initiate_sign"));
        request.setAuthorizeConfig(authorizeConfig);

        OrgAuthUrlRequest.RedirectConfig redirectConfig = new OrgAuthUrlRequest.RedirectConfig();
        redirectConfig.setRedirectUrl(esignProperties.getRedirectUri());
        request.setRedirectConfig(redirectConfig);
        request.setClientType("ALL");
        request.setNotifyUrl(String.format(esignProperties.getNotify(), companyId, userId));

        return esignAuthApi.createOrgAuthUrl(request);
    }

}
