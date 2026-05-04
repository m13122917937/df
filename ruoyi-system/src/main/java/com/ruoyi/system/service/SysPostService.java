package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.mapper.SysPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysPostService extends ServiceImpl<SysPostMapper, SysPost> {
    @Autowired
    private SysPostMapper sysPostMapper;

}
