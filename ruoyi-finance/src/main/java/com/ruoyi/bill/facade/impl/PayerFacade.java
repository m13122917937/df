package com.ruoyi.bill.facade.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.bill.domain.Payer;
import com.ruoyi.bill.facade.IPayerFacade;
import com.ruoyi.bill.model.query.PayerQuery;
import com.ruoyi.bill.model.bo.PayerBO;
import com.ruoyi.bill.model.param.PayerParam;
import com.ruoyi.bill.convert.PayerCov;
import com.ruoyi.bill.service.PayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 付款账号维护Service接口
 *
 * @author ruoyi
 * @date 2025-11-07
 */
@Slf4j
@Service
public class PayerFacade implements IPayerFacade {

    @Autowired
    private PayerService payerService;

    @Override
    public List<PayerBO> list(PayerQuery query, SortBy sort) {

        return PayerCov.INSTANCE.listToBO(payerService.list(DynamicCondition.toWrapper(query)));
    }


    @Override
    public PageBO<PayerBO> listPage(final PayerQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        Wrapper<Payer> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
        return PageUtils.fromList(payerService.list(wrapper), PayerCov.INSTANCE::listToBO);
    }


    @Override
    public PayerBO getOne(PayerQuery query) {
        return PayerCov.INSTANCE.toBO(payerService.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public boolean save(PayerParam param) {
        boolean save = payerService.save(PayerCov.INSTANCE.paramToDomain(param));
        return save;
    }

    @Override
    public boolean update(PayerParam param, PayerQuery query) {
        return payerService.update(PayerCov.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }

}
