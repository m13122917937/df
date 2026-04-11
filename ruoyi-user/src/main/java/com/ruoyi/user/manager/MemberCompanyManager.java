package com.ruoyi.user.manager;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.user.domain.MemberCompany;
import com.ruoyi.user.mapper.MemberCompanyMapper;
import com.ruoyi.user.model.query.MemberCompanyQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员企业Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-06
 */
@Service
public class MemberCompanyManager extends ServiceImpl<MemberCompanyMapper, MemberCompany> {
    @Autowired
    private MemberCompanyMapper memberCompanyMapper;


    public List<MemberCompany> list(Long userId) {
        Wrapper<MemberCompany> wrapper = DynamicCondition.toWrapper(new MemberCompanyQuery().setUserId(userId));
        return this.list(wrapper);
    }

}
