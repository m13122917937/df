package com.ruoyi.system.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.mapper.SysLogininforMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysLogininforManager extends ServiceImpl<SysLogininforMapper, SysLogininfor> {
    @Autowired
    private SysLogininforMapper sysLogininforMapper;

}
