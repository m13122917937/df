package com.ruoyi.biz.company;

import cn.hutool.core.lang.Assert;
import com.ruoyi.esign.api.EsignAuthApi;
import com.ruoyi.esign.model.v3.OrgAuthUrlRequest;
import com.ruoyi.esign.properties.EsignProperties;
import com.ruoyi.mapper.user.CompanyConvert;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.web.vo.user.CompanyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
     * 获取机构认证授权页面链接
     *
     * @param companyId 企业ID
     * @param userId    用户ID（办理人在你方平台的唯一标识）
     * @return 认证跳转URL
     */
    public String getOrgAuthUrl(Long companyId, Long userId) {
        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(companyId));
        Assert.notNull(companyBO, "企业不存在");

        OrgAuthUrlRequest request = new OrgAuthUrlRequest();
        // 第三方平台用户唯一标识
        request.setThirdPartyUserId(String.valueOf(userId));
        // 机构在第三方平台的唯一标识（使用企业ID）
        request.setOrgThirdUniqueId(String.valueOf(companyId));
        // 机构名称
        request.setOrgName(companyBO.getCompanyName());
        // 证件类型：统一社会信用代码
        request.setOrgCardType("CREDIT_CODE");
        // 证件号码
        request.setOrgCardNo(companyBO.getCreditCode());
        // 自动进入认证
        request.setAuthorizeAuto(true);
        // 认证链接有效期 120分钟
        request.setExpire(120);
        // 设置认证上下文（法定代表人信息）
        OrgAuthUrlRequest.Context context = new OrgAuthUrlRequest.Context();
        context.setLegalRepName(companyBO.getCorporateName());
        request.setContext(context);
        // 机构认证配置：认证完成后自动跳转
        OrgAuthUrlRequest.OrgAuthConfig orgAuthConfig = new OrgAuthUrlRequest.OrgAuthConfig();
        orgAuthConfig.setAutoJump(true);
        request.setOrgAuthConfig(orgAuthConfig);
        // 重定向地址
        request.setRedirectUrl(esignProperties.getRedirectUri());
        request.setNotifyUrl(String.format(esignProperties.getNotify(), companyId));
        // 业务流水号
        request.setSerialNo(String.valueOf(System.currentTimeMillis()));

        return esignAuthApi.createOrgAuthUrl(request);
    }

}
