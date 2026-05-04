package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysExcelTask;
import com.ruoyi.system.mapper.SysExcelTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysExcelTaskService extends ServiceImpl<SysExcelTaskMapper, SysExcelTask> {
    @Autowired
    private SysExcelTaskMapper sysExcelTaskMapper;

}
