package com.ruoyi.user.facade.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.user.convert.CompanyCov;
import com.ruoyi.user.domain.Company;
import com.ruoyi.user.domain.User;
import com.ruoyi.user.domain.UserCompany;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.manager.CompanyManager;
import com.ruoyi.user.manager.UserCompanyManager;
import com.ruoyi.user.manager.UserManager;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.consts.UserEnum;
import com.ruoyi.user.model.param.CompanyParam;
import com.ruoyi.user.model.param.UserCompanyParam;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.user.model.query.UserCompanyQuery;
import com.ruoyi.user.model.query.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CompanyFacadeService implements ICompanyFacade {

    @Autowired
    CompanyManager companyManager;

    @Autowired
    UserCompanyManager userCompanyManager;

    @Autowired
    UserManager userManager;

    @Override
    public List<CompanyBO> list(final CompanyQuery query) {

        List<Company> list = companyManager.list(DynamicCondition.toWrapper(query));
        return CompanyCov.INSTANCE.toBOList(list);

    }

    @Override
    public PageBO<CompanyBO> listPage(final CompanyQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<Company> list = companyManager.list(DynamicCondition.toWrapper(query));
        return PageUtils.fromList(list, CompanyCov.INSTANCE::toBOList);
    }


    @Override
    public CompanyBO queryOne(final CompanyQuery query) {
        Company one = companyManager.getOne(DynamicCondition.toWrapper(query));
        return CompanyCov.INSTANCE.toBO(one);
    }

    @Override
    public CompanyBO add(final CompanyParam companyParam) {
        Company company = CompanyCov.INSTANCE.toDomain(companyParam);
        Company one = companyManager.getOne(new LambdaQueryWrapper<Company>().eq(Company::getCompanyName, companyParam.getCompanyName()));
        if (Objects.nonNull(one)) {
            throw new ServiceException("公司已存在");
        }
        DateTime date = DateUtil.date();
        company.setCreateTime(date);
        company.setUpdateTime(date);
        boolean save = companyManager.save(company);
        return CompanyCov.INSTANCE.toBO(company);
    }

    @Override
    public User companyMasterUser(Long companyId) {
        List<UserCompany> list = userCompanyManager.list(new LambdaQueryWrapper<UserCompany>().eq(UserCompany::getCompanyId, companyId).eq(UserCompany::getOwner, UserEnum.UserOwner.MASTER.getValue()));
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        UserCompany userCompany = list.get(0);
        return userManager.getOne(DynamicCondition.toWrapper(new UserQuery().setUserId(userCompany.getUserId())));
    }


    @Override
    public List<CompanyBO> queryList(long userId) {

        List<UserCompany> list = userCompanyManager.list(userId);
        if (CollectionUtil.isEmpty(list)) {
            return List.of();
        }
        List<Long> companyIds = list.stream().map(UserCompany::getCompanyId).collect(Collectors.toList());
        List<Company> companyList = companyManager.list(new LambdaQueryWrapper<Company>().in(Company::getId, companyIds));
        List<CompanyBO> boList = CompanyCov.INSTANCE.toBOList(companyList);
        for (CompanyBO companyBO : boList) {
            companyBO.setOwner(list.stream().filter(userCompany -> userCompany.getCompanyId().equals(companyBO.getId())).findFirst().get().getOwner());
        }
        return boList;
    }

    @Override
    public void removeUserCompany(Long companyId, Long userId) {
        userCompanyManager.remove(new LambdaQueryWrapper<UserCompany>().eq(UserCompany::getCompanyId, companyId).eq(UserCompany::getUserId, userId));
    }

    @Override
    public void update(CompanyParam companyParam, CompanyQuery companyQuery) {
        companyManager.update(CompanyCov.INSTANCE.paramToDomain(companyParam), DynamicCondition.toWrapper(companyQuery));
    }

    @Override
    public void update(UserCompanyParam userCompanyParam, UserCompanyQuery userCompanyQuery) {
        userCompanyManager.update(CompanyCov.INSTANCE.paramToUserDomain(userCompanyParam), DynamicCondition.toWrapper(userCompanyQuery));
    }

    @Override
    public Long countUser(UserCompanyQuery userCompanyQuery) {
        return userCompanyManager.count(DynamicCondition.toWrapper(userCompanyQuery));
    }
}
