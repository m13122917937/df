package com.ruoyi.bill.facade.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.bill.convert.DeductionCov;
import com.ruoyi.bill.domain.Deduction;
import com.ruoyi.bill.facade.IDeductionFacade;
import com.ruoyi.bill.manager.DeductionManager;
import com.ruoyi.bill.model.bo.DeductionBO;
import com.ruoyi.bill.model.param.DeductionParam;
import com.ruoyi.bill.model.query.DeductionQuery;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2025-12-10
 */
 @Slf4j
 @Service
public class DeductionFacadeService implements IDeductionFacade {

    @Autowired
    private DeductionManager deductionManager;

    @Override
    public List<DeductionBO> list(DeductionQuery query, SortBy sort) {

        return DeductionCov.INSTANCE.listToBO(deductionManager.list(DynamicCondition.toWrapper( query)));
    }


     @Override
     public PageBO<DeductionBO> listPage(final DeductionQuery query, final PageParamV2 pageParam) {
         PageUtils.startPage(pageParam);
         Wrapper<Deduction> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
         return PageUtils.fromList(deductionManager.list(wrapper),  DeductionCov.INSTANCE::listToBO);
     }

     @Override
     public DeductionBO save(final DeductionParam param) {
         Deduction deduction = DeductionCov.INSTANCE.toEntity(param);
         deductionManager.save(deduction);
         return DeductionCov.INSTANCE.toBO(deduction);
     }


    @Override
    public DeductionBO getOne(DeductionQuery query) {
        return DeductionCov.INSTANCE.toBO(deductionManager.getOne(DynamicCondition.toWrapper( query)));
    }

    @Override
    public boolean update(DeductionParam param,DeductionQuery query) {
        return deductionManager.update(DeductionCov.INSTANCE.paramToDomain(param),DynamicCondition.toWrapper( query));
    }

}
