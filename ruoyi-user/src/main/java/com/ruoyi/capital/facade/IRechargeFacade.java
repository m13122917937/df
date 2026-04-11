package com.ruoyi.capital.facade;

import com.ruoyi.capital.model.bo.RechargeBO;
import com.ruoyi.capital.model.param.RechargeParam;
import com.ruoyi.capital.model.query.RechargeQuery;

public interface IRechargeFacade {

    RechargeBO queryOne(RechargeQuery rechargeQuery);

    boolean update(RechargeParam rechargeParam, RechargeQuery rechargeQuery);

    RechargeBO save(RechargeParam rechargeParam);

}
