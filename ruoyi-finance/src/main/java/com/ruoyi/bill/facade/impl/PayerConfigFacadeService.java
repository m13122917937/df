package com.ruoyi.bill.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;

import java.util.List;

import com.ruoyi.bill.model.param.PayerParam;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.bill.domain.PayerConfig;
import com.ruoyi.bill.facade.IPayerConfigFacade;
import com.ruoyi.bill.model.query.PayerConfigQuery;
import com.ruoyi.bill.model.bo.PayerConfigBO;
import com.ruoyi.bill.model.param.PayerConfigParam;
import com.ruoyi.bill.convert.PayerConfigCov;
import com.ruoyi.bill.manager.PayerConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 付款配置Service接口
 * 
 * @author ruoyi
 * @date 2025-11-07
 */
 @Slf4j
 @Service
public class PayerConfigFacadeService implements IPayerConfigFacade {

    @Autowired
    private PayerConfigManager payerConfigManager;

    @Override
    public List<PayerConfigBO> list(PayerConfigQuery query, SortBy sort) {

        return PayerConfigCov.INSTANCE.listToBO(payerConfigManager.list(DynamicCondition.toWrapper( query)));
    }


     @Override
     public PageBO<PayerConfigBO> listPage(final PayerConfigQuery query, final PageParamV2 pageParam) {
         PageUtils.startPage(pageParam);
         Wrapper<PayerConfig> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
         return PageUtils.fromList(payerConfigManager.list(wrapper),  PayerConfigCov.INSTANCE::listToBO);
     }


    @Override
    public PayerConfigBO getOne(PayerConfigQuery query) {
        return PayerConfigCov.INSTANCE.toBO(payerConfigManager.getOne(DynamicCondition.toWrapper( query)));
    }

    @Override
    public boolean update(PayerConfigParam param,PayerConfigQuery query) {
        return payerConfigManager.update(PayerConfigCov.INSTANCE.paramToDomain(param),DynamicCondition.toWrapper( query));
    }

    @Override
    public void save(PayerConfigParam param) {
        payerConfigManager.save(PayerConfigCov.INSTANCE.paramToDomain(param));
    }

    @Override
    public long count(PayerConfigQuery payerConfigQuery) {
        return payerConfigManager.count(DynamicCondition.toWrapper(payerConfigQuery));
    }

}
