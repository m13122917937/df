package com.ruoyi.system.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysRoleMenu;
import com.ruoyi.system.mapper.SysRoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysRoleMenuManager extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> {
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

}
