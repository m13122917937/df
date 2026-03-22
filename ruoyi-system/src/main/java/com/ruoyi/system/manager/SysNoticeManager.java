package com.ruoyi.system.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.mapper.SysNoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysNoticeManager extends ServiceImpl<SysNoticeMapper, SysNotice> {
    @Autowired
    private SysNoticeMapper sysNoticeMapper;

}
