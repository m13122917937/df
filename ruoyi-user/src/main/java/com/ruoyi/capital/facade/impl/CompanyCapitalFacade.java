package com.ruoyi.capital.facade.impl;

import com.ruoyi.capital.convert.CompanyCapitalCov;
import com.ruoyi.capital.convert.CompanyCapitalLogCov;
import com.ruoyi.capital.domain.CompanyCapital;
import com.ruoyi.capital.domain.CompanyCapitalLog;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.capital.service.CompanyCapitalService;
import com.ruoyi.capital.service.CompanyCapitalLogService;
import com.ruoyi.capital.model.bo.CompanyCapitalBO;
import com.ruoyi.capital.model.bo.CompanyCapitalLogBO;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.capital.model.query.CompanyCapitalLogQuery;
import com.ruoyi.capital.model.query.CompanyCapitalQuery;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompanyCapitalFacade implements ICompanyCapitalFacade {

    private final CompanyCapitalService companyCapitalService;
    private final CompanyCapitalLogService companyCapitalLogService;


    @Override
    public CompanyCapitalBO queryOne(CompanyCapitalQuery query) {

        CompanyCapital companyCapital = companyCapitalService.getOne(DynamicCondition.toWrapper(query));

        return CompanyCapitalCov.INSTANCE.toBO(companyCapital);
    }

    @Override
    public void changeAvailable(final CompanyCapitalLogParam logParam) {
        companyCapitalService.changeAvailable(logParam);
    }

    @Override
    public void freeze(CompanyCapitalLogParam logParam) {
        companyCapitalService.freeze(logParam);
    }


    @Override
    public void unFreeze(CompanyCapitalLogParam logParam) {
        companyCapitalService.unFreeze(logParam);
    }


    @Override
    public PageBO<CompanyCapitalLogBO> pageLog(CompanyCapitalLogQuery query, PageParamV2 pageParamV2) {

        PageUtils.startPage(pageParamV2);

        List<CompanyCapitalLog> list = companyCapitalLogService.list(DynamicCondition.toWrapper(query));


        return PageUtils.fromList(list, CompanyCapitalLogCov.INSTANCE::listToBO);


    }


    @Override
    public List<CompanyCapitalLogBO> listLog(CompanyCapitalLogQuery query) {
        List<CompanyCapitalLog> list = companyCapitalLogService.list(DynamicCondition.toWrapper(query));

        return CompanyCapitalLogCov.INSTANCE.listToBO(list);


    }


}
