package com.ruoyi.bill.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;

import java.util.List;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.bill.domain.BillPayPlan;
import com.ruoyi.bill.facade.IBillPayPlanFacade;
import com.ruoyi.bill.model.query.BillPayPlanQuery;
import com.ruoyi.bill.model.bo.BillPayPlanBO;
import com.ruoyi.bill.model.param.BillPayPlanParam;
import com.ruoyi.bill.convert.BillPayPlanCov;
import com.ruoyi.bill.manager.BillPayPlanManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 排款计划Service接口
 *
 * @author ruoyi
 * @date 2025-11-09
 */
@Slf4j
@Service
public class BillPayPlanFacadeService implements IBillPayPlanFacade {

    @Autowired
    private BillPayPlanManager billPayPlanManager;

    @Override
    public List<BillPayPlanBO> list(BillPayPlanQuery query, SortBy sort) {

        return BillPayPlanCov.INSTANCE.listToBO(billPayPlanManager.list(DynamicCondition.toWrapper(query)));
    }


    @Override
    public PageBO<BillPayPlanBO> listPage(final BillPayPlanQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        Wrapper<BillPayPlan> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
        return PageUtils.fromList(billPayPlanManager.list(wrapper), BillPayPlanCov.INSTANCE::listToBO);
    }


    @Override
    public BillPayPlanBO getOne(BillPayPlanQuery query) {
        return BillPayPlanCov.INSTANCE.toBO(billPayPlanManager.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public boolean delete(BillPayPlanQuery query) {
        return billPayPlanManager.remove(DynamicCondition.toWrapper(query));
    }

    @Override
    public BillPayPlanBO save(BillPayPlanParam param) {
        BillPayPlan billPayPlan = BillPayPlanCov.INSTANCE.paramToDomain(param);
        billPayPlanManager.save(billPayPlan);
        return BillPayPlanCov.INSTANCE.toBO(billPayPlan);
    }

    @Override
    public boolean update(BillPayPlanParam param, BillPayPlanQuery query) {
        return billPayPlanManager.update(BillPayPlanCov.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }

}
