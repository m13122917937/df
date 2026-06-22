package com.ruoyi.biz.company;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.biz.contract.ContractBizService;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.esign.api.EsignAuthApi;
import com.ruoyi.esign.exception.EsignException;
import com.ruoyi.esign.model.v3.OrgIdentityInfoRequest;
import com.ruoyi.esign.model.v3.OrgIdentityInfoResponse;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.vendor.VendorCreateParam;
import com.ruoyi.jky.rep.vendor.VendorCreateRep;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.consts.CompanyEnum;
import com.ruoyi.user.model.param.CompanyParam;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.wangdian.param.base.ProviderParams;
import com.ruoyi.wangdian.utils.WdtClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class CompanyBizService {


    @Autowired
    private WdtClient wdtClient;

    @Autowired
    private JkyTemplate jkyTemplate;

    @Autowired
    private EsignAuthApi esignAuthApi;

    @Autowired
    private ICompanyFacade companyFacade;

    @Autowired
    private ContractBizService contractBizService;


    private static final String OUT_NO_PREFIX = "FY";

    private static final String COMPANY_NAME_AFTER = "@FY";

    private static final String JKY_VENDOR_CLASS_NAME = "BMG国内";

    /**
     * 新增企业。
     */
    public CompanyBO add(CompanyParam companyParam) {
        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setCompanyName(companyParam.getCompanyName()));
        Assert.isNull(companyBO, "企业已经存在，请重新添加");
        companyBO = companyFacade.add(companyParam);
        checkContractAuthStatus(companyBO);
        createProvider(companyBO);
        initFrameworkContract(companyBO, companyParam.getCreatorId());
        return companyBO;
    }

    /**
     * 初始化框架协议草稿：我方主体固定为集团公司
     *
     * @param companyBO 新建的供应商
     * @param createBy  创建人id
     */
    private void initFrameworkContract(CompanyBO companyBO, Long createBy) {
        try {
            contractBizService.initFrameworkContract(companyBO.getId(), companyBO.getCompanyName(), createBy);
        } catch (Exception e) {
            log.error("初始化框架协议失败，companyId:{}，message:{}", companyBO.getId(), e.getMessage(), e);
        }
    }

    /**
     * 检查企业合同认证状态
     *
     * @param companyBO 企业信息
     */
    public void checkContractAuthStatus(CompanyBO companyBO) {
        try {
            OrgIdentityInfoRequest request = new OrgIdentityInfoRequest();
            request.setOrgName(companyBO.getCompanyName());
            OrgIdentityInfoResponse response = esignAuthApi.getOrgIdentityInfo(request);
            if (Objects.nonNull(response) && Objects.equals(response.getRealnameStatus(), CompanyEnum.ContractAuthStatus.USE.getValue())) {
                companyFacade.update(new CompanyParam().setContractAuthStatus(CompanyEnum.ContractAuthStatus.USE.getValue()), new CompanyQuery().setId(companyBO.getId()));
                companyBO.setContractAuthStatus(CompanyEnum.ContractAuthStatus.USE.getValue());
            }
        } catch (EsignException e) {
            log.warn("查询e签宝企业认证状态失败，companyId:{}，companyName:{}，message:{}", companyBO.getId(), companyBO.getCompanyName(), e.getMessage());
        }
    }

    /**
     * 同步企业合同认证状态
     */
    public void syncContractAuthStatus() {
        log.info("开始同步企业e签宝合同认证状态");
        Integer page = 1;
        for (; ; page++) {
            PageBO<CompanyBO> companyPage = companyFacade.listPage(new CompanyQuery().setContractAuthStatus(CompanyEnum.ContractAuthStatus.NO.getValue()), new PageParamV2(page, 100));
            if (CollectionUtil.isEmpty(companyPage.getData())) {
                break;
            }
            for (CompanyBO companyBO : companyPage.getData()) {
                checkContractAuthStatus(companyBO);
            }
        }
        log.info("结束同步企业e签宝合同认证状态");
    }

    /**
     * 创建旺店通供应商
     *
     * @param companyBO 企业信息
     */
    public void createProvider(CompanyBO companyBO) {
        if (Objects.isNull(companyBO.getCompanyName()) || StrUtil.isBlank(companyBO.getCompanyName())) {
            return;
        }
        String providerNo = genOutNo(companyBO);
        companyFacade.update(new CompanyParam().setOutNo(providerNo), new CompanyQuery().setId(companyBO.getId()));
        companyBO.setOutNo(providerNo);
        String providerName = companyBO.getCompanyName() + COMPANY_NAME_AFTER;
        ProviderParams params = ProviderParams.builder().provider_no(providerNo).provider_name(providerName).build();
        try {
            wdtClient.createProvider(params);
        } catch (Exception e) {
            log.warn("创建旺店通供应商失败，companyId:{}，providerNo:{}，providerName:{}，message:{}", companyBO.getId(), params.getProvider_no(), providerName, e.getMessage());
        }
        createJkyVendor(companyBO, providerNo, providerName);
    }

    private void createJkyVendor(CompanyBO companyBO, String providerNo, String providerName) {
        VendorCreateParam param = new VendorCreateParam();
        param.setCode(providerNo);
        param.setName(providerName);
        param.setAbbreviation(companyBO.getCompanyName());
        param.setClassName(JKY_VENDOR_CLASS_NAME);
        try {
            JkyResponse<VendorCreateRep> response = jkyTemplate.createVendor(param);
            if (response == null || !Objects.equals(response.getCode(), 200)) {
                log.warn("创建吉客云供应商失败，companyId:{}，providerNo:{}，providerName:{}，code:{}，msg:{}", companyBO.getId(), providerNo, providerName,
                        response == null ? null : response.getCode(), response == null ? null : response.getMsg());
            }
        } catch (Exception e) {
            log.warn("创建吉客云供应商异常，companyId:{}，providerNo:{}，providerName:{}，message:{}", companyBO.getId(), providerNo, providerName, e.getMessage());
        }
    }

    private String genOutNo(CompanyBO companyBO) {
        if (StrUtil.isBlank(companyBO.getOutNo())) {
            return OUT_NO_PREFIX + companyBO.getId();
        }
        return companyBO.getOutNo();
    }

}

