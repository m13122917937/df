package com.ruoyi.user.facade.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MemberFacade implements IMemberFacade {


    @Autowired
    MemberService memberService;

    @Autowired
    MemberCompanyService memberCompanyService;


    @Override
    public List<MemberBO> queryList(final MemberQuery memberQuery) {

        Member member = MemberCov.INSTANCE.queryToBo(memberQuery);
        List<Member> list = memberService.list(new QueryWrapper<>(member));
        return MemberCov.INSTANCE.domainToBoList(list);
    }

    @Override
    public MemberBO queryOne(final MemberQuery memberQuery) {
        return MemberCov.INSTANCE.domainToBo(memberService.getOne(new QueryWrapper<>(MemberCov.INSTANCE.queryToBo(memberQuery))));
    }

    @Override
    @Transactional
    public MemberBO addMemberAndCompany(final MemberParam memberParam) {

        Member member = MemberCov.INSTANCE.paramToBo(memberParam);
        member.setCreateTime(DateUtil.date()).setDeleted(MemberEnum.UserDeleted.NORMAL.getValue());
        memberService.save(member);
        MemberBO memberBO = MemberCov.INSTANCE.domainToBo(member);
        MemberCompany memberCompany = new MemberCompany().setCompanyId(memberParam.getCompanyId()).setOwner(memberParam.getOwner())
                .setUserId(memberBO.getUserId()).setOwner(memberParam.getOwner()).setCreateTime(DateUtil.date());
        memberCompanyService.save(memberCompany);
        return memberBO;
    }

    @Override
    public PageBO<MemberBO> memberList(final MemberQuery memberQuery, final PageParamV2 pageParamV2) {
        PageUtils.startPage(pageParamV2);
        List<CompanyUserDTO> companyUsers = memberService.companyUser(memberQuery);
        return PageUtils.fromList(companyUsers, MemberCov.INSTANCE::dtoToBO);


    }

    @Override
    public void addMemberCompany(Long memberId, Long companyId, String name, Integer owner) {
        long count = memberCompanyService.count(DynamicCondition.toWrapper(new MemberCompanyQuery().setUserId(memberId).setCompanyId(companyId)));
        if (count > 1) {
            return;
        }
        Integer accountOwner = Objects.requireNonNullElse(owner, MemberEnum.UserOwner.PEOPLE.getValue());
        MemberCompany memberCompany = new MemberCompany().setCompanyId(companyId).setUserId(memberId).setOwner(accountOwner).setCreateTime(DateUtil.date());
        memberCompanyService.save(memberCompany);

        memberService.update(new Member().setNickName(name), DynamicCondition.toWrapper(new MemberQuery().setUserId(memberId)));
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
        List<MemberBO> memberBOList = new ArrayList<>();
        for (MemberCompany memberCompany : memberCompanyList) {
            MemberBO memberBO = MemberCov.INSTANCE.domainToBo(memberMap.get(memberCompany.getUserId()));
            memberBO.setOwner(memberCompany.getOwner());
            memberBOList.add(memberBO);
        }
        return PageUtils.fromList(memberCompanyList, memberCompanies -> memberBOList);
    }

    @Override
    public boolean companyMasterUser(Long companyId, Long currentUserId) {
        long count = memberCompanyService.count(DynamicCondition.toWrapper(new MemberCompanyQuery().setCompanyId(companyId).setUserId(currentUserId).setOwner(MemberEnum.UserOwner.MASTER.getValue())));
        return count > 0;
    }

}
