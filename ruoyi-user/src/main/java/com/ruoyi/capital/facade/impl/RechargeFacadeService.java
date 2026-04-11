package com.ruoyi.capital.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.capital.convert.RechargeCov;
import com.ruoyi.capital.domain.Recharge;
import com.ruoyi.capital.facade.IRechargeFacade;
import com.ruoyi.capital.manager.RechargeManager;
import com.ruoyi.capital.model.bo.RechargeBO;
import com.ruoyi.capital.model.param.RechargeParam;
import com.ruoyi.capital.model.query.RechargeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RechargeFacadeService implements IRechargeFacade {

    @Autowired
    RechargeManager rechargeManager;

    @Override
    public RechargeBO queryOne(final RechargeQuery rechargeQuery) {
        Recharge one = rechargeManager.getOne(new QueryWrapper<>(RechargeCov.INSTANCE.toDomain(rechargeQuery)));
        return RechargeCov.INSTANCE.toBO(one);
    }

    @Override
    public boolean update(final RechargeParam rechargeParam, final RechargeQuery rechargeQuery) {
        return rechargeManager.update(RechargeCov.INSTANCE.paramToDomain(rechargeParam), new QueryWrapper<>(RechargeCov.INSTANCE.toDomain(rechargeQuery)));
    }

    @Override
    public RechargeBO save(final RechargeParam rechargeParam) {
        Recharge recharge = RechargeCov.INSTANCE.paramToDomain(rechargeParam);
        boolean save = rechargeManager.save(recharge);
        return RechargeCov.INSTANCE.toBO(recharge);
    }
}
