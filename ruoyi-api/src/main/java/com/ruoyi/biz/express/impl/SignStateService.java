package com.ruoyi.biz.express.impl;

import com.ruoyi.biz.bill.BillBizService;
import com.ruoyi.biz.express.AbstractLogisticsState;
import com.ruoyi.biz.express.LogisticsState;
import com.ruoyi.biz.express.bean.LogisticsStateParam;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.user.facade.ICompanyFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author nlsm
 * 物流签收状态处理
 */
@Slf4j
@Service
public class SignStateService extends AbstractLogisticsState implements LogisticsState {

    @Autowired
    private ICompanyFacade companyFacade;

    @Autowired
    BillBizService billBizService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doAction(LogisticsStateParam stateParam) {
        String orderId = stateParam.getOrderId();
        String logisticsNo = stateParam.getLogisticsNo();
        log.info("快递已签收-->orderId={}, no={}", orderId, logisticsNo);
        OrderBO orderBo = super.getOrder(orderId);
        if (Objects.isNull(orderBo)) {
            return;
        }
        // 修改签收时间
        OrderParam orderParam = new OrderParam().setStatus(OrderConsts.OrderStatus.ENDING.getCode());
        if (Objects.nonNull(stateParam.getSignDate())) {
            orderParam.setSignedTime(stateParam.getSignDate());
        }
        orderFacade.update(orderParam, new OrderQuery().setOrderCode(orderId));
        // 生成账单
        billBizService.generateBill(orderBo);
        log.info("快递签收处理完成-->orderId={}, no={}", orderId, logisticsNo);

    }


}
