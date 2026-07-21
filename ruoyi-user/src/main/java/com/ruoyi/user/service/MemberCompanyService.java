package com.ruoyi.user.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.user.domain.MemberCompany;
import com.ruoyi.user.mapper.MemberCompanyMapper;
import com.ruoyi.user.model.query.MemberCompanyQuery;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员企业Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-06
 */
@Service
public class MemberCompanyService extends ServiceImpl<MemberCompanyMapper, MemberCompany> {
    /**
     * 按用户查询企业关系。
     *
     * @param userId 用户标识
     * @return 企业关系列表
     */
    public List<MemberCompany> list(Long userId) {
        Wrapper<MemberCompany> wrapper = DynamicCondition.toWrapper(new MemberCompanyQuery().setUserId(userId));
        return list(wrapper);
    }

}
