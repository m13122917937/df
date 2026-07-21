package com.ruoyi.capital.facade;

import com.ruoyi.capital.model.bo.RechargeBO;
import com.ruoyi.capital.model.param.RechargeParam;
import com.ruoyi.capital.model.query.RechargeQuery;

public interface IRechargeFacade {

    /** @return 充值记录 */
    RechargeBO queryOne(RechargeQuery rechargeQuery);

    /** @return 是否更新成功 */
    boolean update(RechargeParam rechargeParam, RechargeQuery rechargeQuery);

    /** @return 已保存的充值记录 */
    RechargeBO save(RechargeParam rechargeParam);

}
