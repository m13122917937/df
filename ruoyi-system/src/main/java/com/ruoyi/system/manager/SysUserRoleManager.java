package com.ruoyi.system.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserRoleManager extends ServiceImpl<SysUserRoleMapper, SysUserRole> {
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

}
