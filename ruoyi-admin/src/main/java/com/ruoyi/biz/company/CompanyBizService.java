package com.ruoyi.biz.company;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.bo.CompanyBankBO;
import com.ruoyi.wangdian.param.base.ProviderParams;
import com.ruoyi.wangdian.utils.WdtClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

@Service
@Slf4j
public class CompanyBizService {


    @Autowired
    private WdtClient wdtClient;

    private static final String OUT_NO_PREFIX = "FY";
    private static final String COMPANY_NAME_AFTER = "@FY";

    public void createProvider(CompanyBO companyBO) throws IOException {
        if (Objects.isNull(companyBO.getCompanyName()) || StrUtil.isBlank(companyBO.getCompanyName())) {
            return;
        }
        companyBO.setCompanyName( companyBO.getCompanyName() + COMPANY_NAME_AFTER);

        ProviderParams params = ProviderParams.builder().provider_no(genOutNo(companyBO)).provider_name(companyBO.getCompanyName()).build();
        wdtClient.createProvider(params);
    }

    private String genOutNo(CompanyBO companyBO) {
        if (StrUtil.isBlank(companyBO.getOutNo())) {
            return OUT_NO_PREFIX + companyBO.getId();
        }
        return companyBO.getOutNo();
    }

}

