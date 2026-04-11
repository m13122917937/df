package com.ruoyi.user.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.param.CompanyParam;
import com.ruoyi.user.model.param.MemberCompanyParam;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.user.model.query.MemberCompanyQuery;

import java.util.List;

/**
 * 企业Service接口
 *
 * @author ruoyi
 * @date 2025-09-06
 */
public interface ICompanyFacade {

    List<CompanyBO> list(CompanyQuery query);

    PageBO<CompanyBO> listPage(final CompanyQuery query, final PageParamV2 pageParam);

    CompanyBO queryOne(CompanyQuery query);

    CompanyBO add(CompanyParam companyParam);

    /**
     * 查询用户x下面所有的部门
     *
     * @param userId
     * @return
     */
    List<CompanyBO> queryList(long userId);

    void removeUserCompany(Long companyId, Long userId);

    void update(CompanyParam companyParam, CompanyQuery companyQuery);

    void update(MemberCompanyParam memberCompanyParam, MemberCompanyQuery userCompanyQuery);



}
