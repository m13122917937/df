package com.ruoyi.user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.user.domain.Member;
import com.ruoyi.user.domain.dto.CompanyUserDTO;
import com.ruoyi.user.mapper.MemberMapper;
import com.ruoyi.user.model.query.MemberQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-06
 */
@Service
public class MemberService  extends ServiceImpl<MemberMapper, Member> {
    @Autowired
    private MemberMapper memberMapper;

    public List<CompanyUserDTO> companyUser(MemberQuery memberQuery) {
        return memberMapper.companyUser(memberQuery);
    }
}
