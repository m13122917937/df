package com.ruoyi.biz.express;


import com.ruoyi.biz.express.bean.LogisticsStateParam;

/**
 * @author nlsm
 * 物流状态变化接口
 */
public interface LogisticsState {

    /**
     * 物流状态操作执行方法
     * @param stateParam 操作入参
     */
    void doAction(LogisticsStateParam stateParam);

}
