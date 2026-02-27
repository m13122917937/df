package com.ruoyi.user.manager;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.user.domain.UserCompany;
import com.ruoyi.user.mapper.UserCompanyMapper;
import com.ruoyi.user.model.query.UserCompanyQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户企业Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-06
 */
@Service
public class UserCompanyManager extends ServiceImpl<UserCompanyMapper, UserCompany> {
    @Autowired
    private UserCompanyMapper userCompanyMapper;


    public List<UserCompany> list(Long userId) {
        Wrapper<UserCompany> wrapper = DynamicCondition.toWrapper(new UserCompanyQuery().setUserId(userId));
        return this.list(wrapper);
    }

}
