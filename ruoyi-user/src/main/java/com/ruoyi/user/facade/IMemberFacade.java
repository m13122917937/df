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

    /** @return 会员列表 */
    List<MemberBO> queryList(MemberQuery memberQuery);

    /** @return 会员详情 */
    MemberBO queryOne(MemberQuery memberQuery);

    /** @return 已创建会员 */
    MemberBO addMemberAndCompany(MemberParam memberParam);

    /** @return 企业成员分页列表 */
    PageBO<MemberBO> memberList(MemberQuery memberQuery, PageParamV2 pageParamV2);

    /** 绑定会员与企业。 */
    void addMemberCompany(Long memberId, Long companyId, String name, Integer owner);

    /** @return 企业成员分页列表 */
    PageBO<MemberBO> pageCompanyMember(MemberQuery memberQuery, PageParamV2 pageParamV2);

    /** @return 当前用户是否为企业主账号 */
    boolean companyMasterUser(Long companyId, Long currentUserId);
}
