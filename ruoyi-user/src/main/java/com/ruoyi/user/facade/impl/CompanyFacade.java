package com.ruoyi.user.facade.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.user.convert.CompanyCov;
import com.ruoyi.user.domain.Company;
import com.ruoyi.user.domain.MemberCompany;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.service.CompanyService;
import com.ruoyi.user.service.MemberCompanyService;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.param.CompanyParam;
import com.ruoyi.user.model.param.MemberCompanyParam;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.user.model.query.MemberCompanyQuery;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyFacade implements ICompanyFacade {

    private final CompanyService companyService;
    private final MemberCompanyService memberCompanyService;


    @Override
    public List<CompanyBO> list(final CompanyQuery query) {

        List<Company> list = companyService.list(DynamicCondition.toWrapper(query));
        return CompanyCov.INSTANCE.toBOList(list);

    }

    @Override
    public PageBO<CompanyBO> listPage(final CompanyQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<Company> list = companyService.list(DynamicCondition.toWrapper(query));
        return PageUtils.fromList(list, CompanyCov.INSTANCE::toBOList);
    }


    @Override
    public CompanyBO queryOne(final CompanyQuery query) {
        Company one = companyService.getOne(DynamicCondition.toWrapper(query));
        return CompanyCov.INSTANCE.toBO(one);
    }

    @Override
    public CompanyBO add(final CompanyParam companyParam) {
        Company company = CompanyCov.INSTANCE.toDomain(companyParam);
        Company one = companyService.getOne(DynamicCondition.toWrapper(
                new CompanyQuery().setCompanyName(companyParam.getCompanyName())));
        if (Objects.nonNull(one)) {
            throw new ServiceException("公司已存在");
        }
        Date date = DateUtil.date();
        company.setCreateTime(date);
        company.setUpdateTime(date);
        boolean save = companyService.save(company);
        return CompanyCov.INSTANCE.toBO(company);
    }


    @Override
    public List<CompanyBO> queryList(long userId) {

        List<MemberCompany> list = memberCompanyService.list(userId);
        if (CollectionUtil.isEmpty(list)) {
            return List.of();
        }
        List<Long> companyIds = list.stream().map(MemberCompany::getCompanyId).collect(Collectors.toList());
        List<Company> companyList = companyService.list(DynamicCondition.toWrapper(
                new CompanyQuery().setIdSet(companyIds)));
        List<CompanyBO> boList = CompanyCov.INSTANCE.toBOList(companyList);
        for (CompanyBO companyBO : boList) {
            companyBO.setOwner(list.stream().filter(userCompany -> userCompany.getCompanyId().equals(companyBO.getId())).findFirst().get().getOwner());
        }
        return boList;
    }

    @Override
    public void removeUserCompany(Long companyId, Long userId) {
        MemberCompanyQuery query = new MemberCompanyQuery().setCompanyId(companyId).setUserId(userId);
        memberCompanyService.remove(DynamicCondition.toWrapper(query));
    }

    @Override
    public void update(CompanyParam companyParam, CompanyQuery companyQuery) {
        companyService.update(CompanyCov.INSTANCE.paramToDomain(companyParam), DynamicCondition.toWrapper(companyQuery));
    }

    @Override
    public void update(MemberCompanyParam memberCompanyParam, MemberCompanyQuery memberCompanyQuery) {
        memberCompanyService.update(CompanyCov.INSTANCE.paramToUserDomain(memberCompanyParam), DynamicCondition.toWrapper(memberCompanyQuery));
    }

}
