package com.ruoyi.biz.express.impl;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.biz.express.AbstractLogisticsState;
import com.ruoyi.biz.express.LogisticsState;
import com.ruoyi.biz.express.bean.LogisticsStateParam;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 正常流转
 * <br>执行循序：揽收时间小于成交时间>系统 订单状态修改为异常
 * @author nlsm
 */
@Slf4j
@Service
public class AnomalyStateService extends AbstractLogisticsState implements LogisticsState {

    @Override
    public void doAction(LogisticsStateParam stateParam) {
        String orderId = stateParam.getOrderId();
        log.info("正常流转---orderId={}，on={}", orderId, stateParam.getLogisticsNo());
        OrderBO orderBo = super.getOrder(orderId);
        if (Objects.isNull(orderBo)) {
            return;
        }
//        //查询物流订单表，是否有该orderId且未揽件状态的订单
//        //修改揽收状态
//        super.updateExpressOrder(orderId);
        // 推送数据的时候查询揽收是否超过24小时
        super.collectTimeout(orderBo, stateParam.getCollectDate(), stateParam.getLogisticsNo());
        // 揽收时间是否小于成交时间
//        boolean isCollect = super.isBeforeCollect(orderBo, stateParam.getCollectDate(), stateParam.getLogisticsNo());
//        if (isCollect) {
//            // 修改订单状态
//            orderFacade.update(new OrderParam().setUpdateTime(DateUtil.date()).setStatus(OrderConsts.OrderStatus.ERROR.getCode())
//                    .setSubStatus(OrderConsts.OrderSubStatus.ERROR_SNED_TIME_KNOTTY.getCode()),new OrderQuery().setOrderCode(orderId));
//        }

    }


}
