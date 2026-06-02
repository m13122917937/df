package com.ruoyi.user.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.user.model.bo.MemberBO;
import com.ruoyi.user.model.param.MemberParam;
import com.ruoyi.user.model.query.MemberQuery;

import java.util.List;

/**
 * 会员信息Service接口
 *
 * @author ruoyi
 * @date 2025-09-06
 */
public interface IMemberFacade {


    List<MemberBO> queryList(MemberQuery memberQuery);


    MemberBO queryOne(MemberQuery memberQuery);


    MemberBO addMemberAndCompany(MemberParam memberParam);


    PageBO<MemberBO> memberList(MemberQuery memberQuery, PageParamV2 pageParamV2);


    void addMemberCompany(Long memberId, Long companyId, String name, Integer owner);


    PageBO<MemberBO> pageCompanyMember(MemberQuery memberQuery, PageParamV2 pageParamV2);


    boolean companyMasterUser(Long companyId, Long currentUserId);
}
