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

    /** @return 企业列表 */
    List<CompanyBO> list(CompanyQuery query);

    /** @return 企业分页列表 */
    PageBO<CompanyBO> listPage(final CompanyQuery query, final PageParamV2 pageParam);

    /** @return 企业详情 */
    CompanyBO queryOne(CompanyQuery query);

    /** @return 已创建企业 */
    CompanyBO add(CompanyParam companyParam);

    /**
     * 查询用户x下面所有的部门
     *
     * @param userId
     * @return
     */
    List<CompanyBO> queryList(long userId);

    /** 移除用户与企业的关系。 */
    void removeUserCompany(Long companyId, Long userId);

    /** 更新企业信息。 */
    void update(CompanyParam companyParam, CompanyQuery companyQuery);

    /** 更新用户在企业内的权限信息。 */
    void update(MemberCompanyParam memberCompanyParam, MemberCompanyQuery userCompanyQuery);



}
