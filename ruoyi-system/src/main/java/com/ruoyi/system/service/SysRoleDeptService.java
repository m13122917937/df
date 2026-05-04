package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysRoleDept;
import com.ruoyi.system.mapper.SysRoleDeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysRoleDeptService extends ServiceImpl<SysRoleDeptMapper, SysRoleDept> {
    @Autowired
    private SysRoleDeptMapper sysRoleDeptMapper;

}
