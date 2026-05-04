package com.ruoyi.system.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;

import com.ruoyi.system.domain.DictDistrict;
import com.ruoyi.system.facade.IDictDistrictFacade;
import com.ruoyi.system.model.query.DictDistrictQuery;
import com.ruoyi.system.model.bo.DictDistrictBO;
import com.ruoyi.system.model.param.DictDistrictParam;
import com.ruoyi.system.convert.DictDistrictCov;
import com.ruoyi.system.service.DictDistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 【省市】Service接口
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Service
public class DictDistrictFacade implements IDictDistrictFacade {

    @Autowired
    private DictDistrictService dictDistrictService;

    @Override
    public List<DictDistrictBO> list(DictDistrictQuery query) {
        DictDistrict domain = DictDistrictCov.INSTANCE.queryToDomain(query);
        return DictDistrictCov.INSTANCE.listToBO(dictDistrictService.list(new QueryWrapper<>(domain)));
    }

    @Override
    public DictDistrictBO getOne(DictDistrictQuery query) {
        DictDistrict domain = DictDistrictCov.INSTANCE.queryToDomain(query);
        return DictDistrictCov.INSTANCE.toBO(dictDistrictService.getOne(new QueryWrapper<>(domain)));
    }

    @Override
    public boolean update(DictDistrictParam param, DictDistrictQuery query) {
        DictDistrict queryDomain = DictDistrictCov.INSTANCE.queryToDomain(query);
        return dictDistrictService.update(DictDistrictCov.INSTANCE.paramToDomain(param), new QueryWrapper<>(queryDomain));
    }

}
