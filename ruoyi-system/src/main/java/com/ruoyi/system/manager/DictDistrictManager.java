package com.ruoyi.system.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.DictDistrict;
import com.ruoyi.system.mapper.DictDistrictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DictDistrictManager extends ServiceImpl<DictDistrictMapper, DictDistrict> {
    @Autowired
    private DictDistrictMapper dictDistrictMapper;

}
