package com.ruoyi.user.facade.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.user.domain.CompanyBank;
import com.ruoyi.user.facade.ICompanyBankFacade;
import com.ruoyi.user.model.query.CompanyBankQuery;
import com.ruoyi.user.model.bo.CompanyBankBO;
import com.ruoyi.user.model.param.CompanyBankParam;
import com.ruoyi.user.convert.CompanyBankCov;
import com.ruoyi.user.service.CompanyBankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 【请填写功能名称】Service接口
 *
 * @author ruoyi
 * @date 2025-09-28
 */
@Slf4j
@Service
public class CompanyBankFacade implements ICompanyBankFacade {

    @Autowired
    private CompanyBankService companyBankService;

    @Override
    public List<CompanyBankBO> list(CompanyBankQuery query, SortBy sort) {

        return CompanyBankCov.INSTANCE.listToBO(companyBankService.list(DynamicCondition.toWrapper(query)));
    }


    @Override
    public PageBO<CompanyBankBO> listPage(final CompanyBankQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        Wrapper<CompanyBank> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
        return PageUtils.fromList(companyBankService.list(wrapper), CompanyBankCov.INSTANCE::listToBO);
    }


    @Override
    public CompanyBankBO getOne(CompanyBankQuery query) {
        return CompanyBankCov.INSTANCE.toBO(companyBankService.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public Long count(CompanyBankQuery query) {
        return companyBankService.count(DynamicCondition.toWrapper(query, null));
    }

    @Override
    public CompanyBankBO save(CompanyBankParam param) {
        CompanyBank companyBank = CompanyBankCov.INSTANCE.paramToDomain(param);
        DateTime date = DateUtil.date();
        companyBank.setCreateTime(date).setUpdateTime(date);
        companyBankService.save(companyBank);
        return CompanyBankCov.INSTANCE.toBO(companyBank);

    }

    @Override
    public boolean update(CompanyBankParam param, CompanyBankQuery query) {
        return companyBankService.update(CompanyBankCov.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }

}
