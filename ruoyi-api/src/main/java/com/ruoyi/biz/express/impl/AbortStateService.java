package com.ruoyi.biz.express.impl;


import com.ruoyi.biz.express.AbstractLogisticsState;
import com.ruoyi.biz.express.LogisticsState;
import com.ruoyi.biz.express.bean.LogisticsStateParam;
import com.ruoyi.biz.express.bean.ShippedErrorStatus;
import com.ruoyi.kuaidi100.model.consts.LogisticsStatus;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.OrderConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author nlsm
 * 3天查询无记录”或“60天无变化”状态处理
 */
@Slf4j
@Service
public class AbortStateService extends AbstractLogisticsState implements LogisticsState {

    @Override
    public void doAction(LogisticsStateParam stateParam) {
        String logisticsNo = stateParam.getLogisticsNo();
        String orderId = stateParam.getOrderId();
        log.info("物流无流转---orderId={}，on={}", orderId, logisticsNo);
        OrderBO orderBo = super.getOrder(orderId);
        if (Objects.isNull(orderBo)) {
            return;
        }
        // 只处理默认订单
        if (Objects.equals(OrderConsts.OrderSubStatus.ERROR_LOGISTICS_CIRCULATION.getCode(), orderBo.getSubStatus())) {
            log.info("订单已经处于物流无流转，无需变化");
            return;
        }
        // 修改异常时间
        ShippedErrorStatus errorStatus = ShippedErrorStatus.NO_EXCHANGE_ERROR;
        if (LogisticsStatus.query(stateParam.getState())) {
            // 判断是否是手机号异常
            if (LogisticsStatus.phone(stateParam.getState())) {
                errorStatus = ShippedErrorStatus.PHONE_ERROR;
            }
        }
        // 修改订单状态
        super.updateLogisticsStatus(orderId, errorStatus);
    }
}
