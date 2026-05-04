package com.ruoyi.capital.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.capital.convert.RechargeCov;
import com.ruoyi.capital.domain.Recharge;
import com.ruoyi.capital.facade.IRechargeFacade;
import com.ruoyi.capital.service.RechargeService;
import com.ruoyi.capital.model.bo.RechargeBO;
import com.ruoyi.capital.model.param.RechargeParam;
import com.ruoyi.capital.model.query.RechargeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RechargeFacade implements IRechargeFacade {

    @Autowired
    RechargeService rechargeService;

    @Override
    public RechargeBO queryOne(final RechargeQuery rechargeQuery) {
        Recharge one = rechargeService.getOne(new QueryWrapper<>(RechargeCov.INSTANCE.toDomain(rechargeQuery)));
        return RechargeCov.INSTANCE.toBO(one);
    }

    @Override
    public boolean update(final RechargeParam rechargeParam, final RechargeQuery rechargeQuery) {
        return rechargeService.update(RechargeCov.INSTANCE.paramToDomain(rechargeParam), new QueryWrapper<>(RechargeCov.INSTANCE.toDomain(rechargeQuery)));
    }

    @Override
    public RechargeBO save(final RechargeParam rechargeParam) {
        Recharge recharge = RechargeCov.INSTANCE.paramToDomain(rechargeParam);
        boolean save = rechargeService.save(recharge);
        return RechargeCov.INSTANCE.toBO(recharge);
    }
}
