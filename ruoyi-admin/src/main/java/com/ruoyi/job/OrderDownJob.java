package com.ruoyi.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.OrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component("orderDownJob")
public class OrderDownJob {

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    /**
     * 每天晚上6点下架全部订单
     * <p>
     * 1、修改订单状态（未抢单的订单）
     * 2、修改挂单状态
     *
     */
    @Transactional
    public void execute() {
        log.info("开始执行订单下架定时任务");
        Integer page = 1;
        for (; ; page++) {
            PageParamV2 pageParamV2 = new PageParamV2(page, 100);
            PageBO<OrderBO> orderBOPageBO = orderFacade.listPage(new OrderQuery().setStatus(OrderConsts.OrderStatus.TRADING.getCode()), pageParamV2);
            if (CollectionUtil.isEmpty(orderBOPageBO.getData())) {
                break;
            }
            for (OrderBO orderBO : orderBOPageBO.getData()) {
                // 删除挂单
                boolean update = hangingOrderFacade.update(new HangingOrderParam().setStatus(HandingOrderConsts.Status.FAILURE.getCode()).setUpdateTime(DateUtil.date()), new HangingOrderQuery().setOrderId(orderBO.getOrderCode())
                        .setStatus(HandingOrderConsts.Status.NORMAL.getCode()).setPriceHighestStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode()).setPriceLowestStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode())
                        .setPriceHignStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode()).setPriceLowStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode()));
                if (!update) {
                    continue;
                }
                // 设置订单到待发布中
                orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.WAIT.getCode()).setSubStatus(OrderConsts.OrderSubStatus.WAIT_TIMEOUT.getCode()).setUpdateTime(DateUtil.date()),
                        new OrderQuery().setOrderCode(orderBO.getOrderCode()));

            }
        }

        log.info("结束执行订单下架定时任务");
    }


}
