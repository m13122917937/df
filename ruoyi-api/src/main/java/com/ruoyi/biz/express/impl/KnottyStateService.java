package com.ruoyi.biz.express.impl;


import com.ruoyi.biz.express.AbstractLogisticsState;
import com.ruoyi.biz.express.LogisticsState;
import com.ruoyi.biz.express.bean.LogisticsStateParam;
import com.ruoyi.biz.express.bean.ShippedErrorStatus;
import com.ruoyi.order.model.bo.OrderBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author nlsm
 * 快递疑难处理
 */
@Slf4j
@Service
public class KnottyStateService extends AbstractLogisticsState implements LogisticsState {

    @Override
    public void doAction(LogisticsStateParam stateParam) {
        String logisticsNo = stateParam.getLogisticsNo();
        String orderId = stateParam.getOrderId();
        log.info("疑难处理---orderId={}，on={}", orderId, logisticsNo);
        OrderBO orderBo = super.getOrder(orderId);
        if (Objects.isNull(orderBo)) {
            return;
        }
        // 作废申请
//        super.clearApply(orderId, logisticsNo);
//         修改异常时间
//        super.abnormalDeductionCapital(orderId, logisticsNo);
        // 修改订单状态
        super.updateLogisticsStatus(orderId, ShippedErrorStatus.KNOTTY_ERROR);
    }
}
