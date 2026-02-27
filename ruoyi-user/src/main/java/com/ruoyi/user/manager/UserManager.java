package com.ruoyi.user.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.user.domain.User;
import com.ruoyi.user.domain.dto.CompanyUserDTO;
import com.ruoyi.user.mapper.UserMapper;
import com.ruoyi.user.model.query.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-06
 */
@Service
public class UserManager  extends ServiceImpl<UserMapper, User> {
    @Autowired
    private UserMapper userMapper;


    public List<CompanyUserDTO> companyUser(UserQuery userQuery) {
        return userMapper.companyUser(userQuery);
    }
}
