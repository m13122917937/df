package com.ruoyi.user.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.user.domain.Member;
import com.ruoyi.user.domain.MemberCompany;
import com.ruoyi.user.domain.dto.CompanyUserDTO;
import com.ruoyi.user.mapper.MemberCompanyMapper;
import com.ruoyi.user.mapper.MemberMapper;
import com.ruoyi.user.model.consts.MemberEnum;
import com.ruoyi.user.model.query.MemberCompanyQuery;
import com.ruoyi.user.model.query.MemberQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * 会员信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-06
 */
@Service
@RequiredArgsConstructor
public class MemberService  extends ServiceImpl<MemberMapper, Member> {
    private final MemberMapper memberMapper;
    private final MemberCompanyMapper memberCompanyMapper;

    /**
     * 新增会员并绑定企业。
     *
     * @param member 会员信息
     * @param companyId 企业主键
     * @param owner 负责人类型
     * @return 新增后的会员
     */
    @Transactional(rollbackFor = Exception.class)
    public Member addMemberAndCompany(Member member, Long companyId, Integer owner) {
        member.setCreateTime(DateUtil.date()).setDeleted(MemberEnum.UserDeleted.NORMAL.getValue());
        save(member);
        MemberCompany memberCompany = new MemberCompany().setCompanyId(companyId).setOwner(owner)
                .setUserId(member.getUserId()).setCreateTime(DateUtil.date());
        memberCompanyMapper.insert(memberCompany);
        return member;
    }

    /**
     * 为已有会员绑定企业并更新昵称。
     *
     * @param memberId 会员主键
     * @param companyId 企业主键
     * @param name 会员昵称
     * @param owner 负责人类型
     */
    @Transactional(rollbackFor = Exception.class)
    public void addMemberCompany(Long memberId, Long companyId, String name, Integer owner) {
        long count = memberCompanyMapper.selectCount(DynamicCondition.toWrapper(
                new MemberCompanyQuery().setUserId(memberId).setCompanyId(companyId)));
        if (count > 0) {
            return;
        }
        Integer accountOwner = Objects.requireNonNullElse(owner, MemberEnum.UserOwner.PEOPLE.getValue());
        MemberCompany memberCompany = new MemberCompany().setCompanyId(companyId).setUserId(memberId)
                .setOwner(accountOwner).setCreateTime(DateUtil.date());
        memberCompanyMapper.insert(memberCompany);
        update(new Member().setNickName(name), DynamicCondition.toWrapper(new MemberQuery().setUserId(memberId)));
    }

    /**
     * 查询企业会员信息。
     *
     * @param memberQuery 查询条件
     * @return 企业会员列表
     */
    public List<CompanyUserDTO> companyUser(MemberQuery memberQuery) {
        return memberMapper.companyUser(memberQuery);
    }
}
