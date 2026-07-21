package com.ruoyi.user.facade.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.user.convert.MemberCov;
import com.ruoyi.user.domain.Member;
import com.ruoyi.user.domain.MemberCompany;
import com.ruoyi.user.domain.dto.CompanyUserDTO;
import com.ruoyi.user.facade.IMemberFacade;
import com.ruoyi.user.service.MemberCompanyService;
import com.ruoyi.user.service.MemberService;
import com.ruoyi.user.model.bo.MemberBO;
import com.ruoyi.user.model.consts.MemberEnum;
import com.ruoyi.user.model.param.MemberParam;
import com.ruoyi.user.model.query.MemberCompanyQuery;
import com.ruoyi.user.model.query.MemberQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberFacade implements IMemberFacade {

    private final MemberService memberService;
    private final MemberCompanyService memberCompanyService;


    @Override
    public List<MemberBO> queryList(final MemberQuery memberQuery) {

        List<Member> list = memberService.list(DynamicCondition.toWrapper(memberQuery));
        return MemberCov.INSTANCE.domainToBoList(list);
    }

    @Override
    public MemberBO queryOne(final MemberQuery memberQuery) {
        return MemberCov.INSTANCE.domainToBo(memberService.getOne(DynamicCondition.toWrapper(memberQuery)));
    }

    @Override
    public MemberBO addMemberAndCompany(final MemberParam memberParam) {
        Member member = MemberCov.INSTANCE.paramToDomain(memberParam);
        Member saved = memberService.addMemberAndCompany(member, memberParam.getCompanyId(), memberParam.getOwner());
        return MemberCov.INSTANCE.domainToBo(saved);
    }

    @Override
    public PageBO<MemberBO> memberList(final MemberQuery memberQuery, final PageParamV2 pageParamV2) {
        PageUtils.startPage(pageParamV2);
        List<CompanyUserDTO> companyUsers = memberService.companyUser(memberQuery);
        return PageUtils.fromList(companyUsers, MemberCov.INSTANCE::dtoToBO);


    }

    @Override
    public void addMemberCompany(Long memberId, Long companyId, String name, Integer owner) {
        memberService.addMemberCompany(memberId, companyId, name, owner);
    }

    @Override
    public PageBO<MemberBO> pageCompanyMember(MemberQuery memberQuery, PageParamV2 pageParamV2) {

        PageUtils.startPage(pageParamV2);
        List<MemberCompany> memberCompanyList = memberCompanyService.list(DynamicCondition.toWrapper(new MemberCompanyQuery().setCompanyId(memberQuery.getCompanyId())));
        if (CollectionUtil.isEmpty(memberCompanyList)) {
            return PageBO.empty();
        }
        Set<Long> userIdSet = memberCompanyList.stream().map(MemberCompany::getUserId).collect(Collectors.toSet());
        List<Member> memberList = memberService.list(DynamicCondition.toWrapper(new MemberQuery().setUserIdSet(new ArrayList<>(userIdSet))));
        Map<Long, Member> memberMap = memberList.stream().collect(Collectors.toMap(Member::getUserId, u -> u));
        List<MemberBO> memberBOList = memberCompanyList.stream()
                .map(memberCompany -> toMemberBO(memberCompany, memberMap))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return PageUtils.fromList(memberBOList, members -> members);
    }

    @Override
    public boolean companyMasterUser(Long companyId, Long currentUserId) {
        long count = memberCompanyService.count(DynamicCondition.toWrapper(new MemberCompanyQuery().setCompanyId(companyId).setUserId(currentUserId).setOwner(MemberEnum.UserOwner.MASTER.getValue())));
        return count > 0;
    }

    private MemberBO toMemberBO(MemberCompany memberCompany, Map<Long, Member> memberMap) {
        Member member = memberMap.get(memberCompany.getUserId());
        if (member == null) {
            log.warn("会员企业关系存在无效会员，userId={}", memberCompany.getUserId());
            return null;
        }
        MemberBO memberBO = MemberCov.INSTANCE.domainToBo(member);
        memberBO.setOwner(memberCompany.getOwner());
        return memberBO;
    }

}
