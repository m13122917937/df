package com.ruoyi.biz.mq;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.HangingOrderBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.param.TradeOrderParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Slf4j
@Getter
@Component
public class OrderTradeListener implements MessageListener {


    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    @Autowired
    IOrderFacade orderFacade;

    @Override
    @Transactional
    public Action consume(Message message, ConsumeContext context) {
        long tradeId = Convert.bytesToLong(message.getBody());
        //
        log.info("订阅到消息:{}", tradeId);
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setId(tradeId));
        if (Objects.isNull(tradeOrderBO)) {
            log.info("{},订单成交记录不存在", tradeOrderBO.getOrderId());
            return Action.CommitMessage;
        }
        if (!Objects.equals(tradeOrderBO.getStatus(), TradeOrderConsts.TradeStatus.EXPIRED.getCode())) {
            log.info("{},订单抢单已经更新，{}", tradeOrderBO.getOrderId(), tradeOrderBO.getOrderId());
            return Action.CommitMessage;
        }
        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setId(tradeOrderBO.getHangOrderId()));
        if (Objects.isNull(hangingOrderBO)) {
            log.info("{},订单挂单已经更新", tradeOrderBO.getOrderId());
            return Action.CommitMessage;
        }
        if (!Objects.equals(hangingOrderBO.getStatus(), HandingOrderConsts.Status.NORMAL.getCode())) {
            log.info("{},订单挂单已经更新", tradeOrderBO.getOrderId());
            return Action.CommitMessage;
        }
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(tradeOrderBO.getOrderId()));

        if (Objects.isNull(orderBO) || !Objects.equals(orderBO.getStatus(), OrderConsts.OrderStatus.TRADING.getCode())) {
            log.info("{},订单已经更新", tradeOrderBO.getOrderId());
            return Action.CommitMessage;
        }


        // 更新交易订单状态
        boolean update = tradeOrderFacade.update(new TradeOrderParam().setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()),
                new TradeOrderQuery().setOrderId(tradeOrderBO.getOrderId()).setStatus(TradeOrderConsts.TradeStatus.EXPIRED.getCode()));
        if (!update) {
            log.info("{},订单成交记录已经更新", tradeOrderBO.getOrderId());
            return Action.CommitMessage;
        }
        // 更新挂单状态
        update = hangingOrderFacade.update(hangingPriceStatus(hangingOrderBO, tradeOrderBO.getTradeUserId()),
                new HangingOrderQuery().setId(tradeOrderBO.getHangOrderId()));
        if (!update) {
            log.info("更新挂单状态失败：{}, 订单价格：{}", tradeOrderBO.getOrderId(), tradeOrderBO.getTradePrice());
            throw new ServiceException("更新挂单状态失败");
        }
        // 更新订单状态, 待填写串码状态
        update = orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.DELIVERY_ING.getCode()).setSubStatus(OrderConsts.OrderSubStatus.WAIT_IMEI.getCode()),
                new OrderQuery().setOrderCode(tradeOrderBO.getOrderId()).setStatus(OrderConsts.OrderStatus.TRADING.getCode()));
        if (!update) {
            log.info("更新订单状态失败：{}, 订单价格：{}", tradeOrderBO.getOrderId(), tradeOrderBO.getTradePrice());
            throw new ServiceException("更新订单状态失败");
        }
        return Action.CommitMessage;
    }


    /**
     *
     * @param hangingOrderBO
     * @return
     */
    private HangingOrderParam hangingPriceStatus(HangingOrderBO hangingOrderBO, Long userId) {
        HangingOrderParam hangingOrderParam = new HangingOrderParam();
        if (Objects.equals(hangingOrderBO.getPriceHighestStatus(), TradeOrderConsts.TradeStatus.EXPIRED.getCode())) {
            hangingOrderParam.setPriceHighestStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode());
        }
        if (Objects.equals(hangingOrderBO.getPriceHignStatus(), TradeOrderConsts.TradeStatus.EXPIRED.getCode())) {
            hangingOrderParam.setPriceHignStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode());
        }
        if (Objects.equals(hangingOrderBO.getPriceLowStatus(), TradeOrderConsts.TradeStatus.EXPIRED.getCode())) {
            hangingOrderParam.setPriceLowStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode());
        }
        if (Objects.equals(hangingOrderBO.getPriceLowestStatus(), TradeOrderConsts.TradeStatus.EXPIRED.getCode())) {
            hangingOrderParam.setPriceLowestStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode());
        }
        hangingOrderParam.setUpdateTime(DateUtil.date()).setUpdateBy(userId);
        return hangingOrderParam;

    }


}
