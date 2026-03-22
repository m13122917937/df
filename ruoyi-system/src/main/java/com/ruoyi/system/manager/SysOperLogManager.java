package com.ruoyi.system.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.mapper.SysOperLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysOperLogManager extends ServiceImpl<SysOperLogMapper, SysOperLog> {
    @Autowired
    private SysOperLogMapper sysOperLogMapper;

}
