package com.ruoyi.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component("orderTransitJob")
public class OrderTransitJob {

    @Autowired
    IOrderFacade orderFacade;


    /**
     * 当日发货订单转在途订单
     *
     */
    @Transactional
    public void execute() {

        log.info("开始执行订单中转定时任务");
        // 设置前两天的定时订单
        DateTime dateTime = DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -2));
        boolean update = orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.TRANSIT.getCode()),
                new OrderQuery().setStatus(OrderConsts.OrderStatus.DELIVERY_END.getCode()).setCreateTimeStart(dateTime));

        log.info("结束执行订单中转定时任务");

    }

}
