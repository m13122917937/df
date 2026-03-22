package com.ruoyi.system.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysConfigManager extends ServiceImpl<SysConfigMapper, SysConfig> {
    @Autowired
    private SysConfigMapper sysConfigMapper;

}
