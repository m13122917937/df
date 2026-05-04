package com.ruoyi.express.facade.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.express.domain.ExpressContrast;
import com.ruoyi.express.facade.IExpressContrastFacade;
import com.ruoyi.express.model.query.ExpressContrastQuery;
import com.ruoyi.express.model.bo.ExpressContrastBO;
import com.ruoyi.express.model.param.ExpressContrastParam;
import com.ruoyi.express.convert.ExpressContrastCov;
import com.ruoyi.express.service.ExpressContrastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 【请填写功能名称】Service接口
 *
 * @author ruoyi
 * @date 2025-10-11
 */
 @Slf4j
 @Service
public class ExpressContrastFacade implements IExpressContrastFacade {

    @Autowired
    private ExpressContrastService expressContrastService;

    @Override
    public List<ExpressContrastBO> list(ExpressContrastQuery query, SortBy sort) {

        return ExpressContrastCov.INSTANCE.listToBO(expressContrastService.list(DynamicCondition.toWrapper( query)));
    }


     @Override
     public PageBO<ExpressContrastBO> listPage(final ExpressContrastQuery query, final PageParamV2 pageParam) {
         PageUtils.startPage(pageParam);
         Wrapper<ExpressContrast> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
         return PageUtils.fromList(expressContrastService.list(wrapper),  ExpressContrastCov.INSTANCE::listToBO);
     }


    @Override
    public ExpressContrastBO getOne(ExpressContrastQuery query) {
        return ExpressContrastCov.INSTANCE.toBO(expressContrastService.getOne(DynamicCondition.toWrapper( query)));
    }

    @Override
    public boolean update(ExpressContrastParam param,ExpressContrastQuery query) {
        return expressContrastService.update(ExpressContrastCov.INSTANCE.paramToDomain(param),DynamicCondition.toWrapper( query));
    }

}
