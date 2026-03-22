package com.ruoyi.system.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysUserPost;
import com.ruoyi.system.mapper.SysUserPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserPostManager extends ServiceImpl<SysUserPostMapper, SysUserPost> {
    @Autowired
    private SysUserPostMapper sysUserPostMapper;

}
