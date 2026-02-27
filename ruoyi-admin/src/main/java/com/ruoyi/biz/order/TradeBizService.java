package com.ruoyi.biz.order;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.ruoyi.mapper.order.TradeConvert;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.TradePriceBO;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.web.form.order.TradePriceForm;
import com.ruoyi.web.vo.order.TradePriceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TradeBizService {

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    /**
     * 查询今天成交数据
     */
    public List<TradePriceVO> today(TradePriceForm tradePriceForm) {
        DateTime date = DateUtil.date();

        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(tradePriceForm.getOrderCode()));
        Assert.notNull(orderBO, "订单不存在");
        DateTime start = DateUtil.beginOfDay(date);
        DateTime end = DateUtil.endOfDay(date);
        TradeOrderQuery tradeOrderQuery = new TradeOrderQuery().setSkuCode(orderBO.getSkuCode())
                .setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()).setStartCreateTime(start).setEndCreateTime(end);
        List<TradePriceBO> tradePriceBOS = tradeOrderFacade.tradePrice(tradeOrderQuery);

        return TradeConvert.INSTANCE.toTradePriceVO(tradePriceBOS);
    }

    /**
     * 查询今天成交数据
     */
    public List<TradePriceVO> yesterday(TradePriceForm tradePriceForm) {

        DateTime date = DateUtil.yesterday();

        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(tradePriceForm.getOrderCode()));
        Assert.notNull(orderBO, "订单不存在");
        DateTime start = DateUtil.beginOfDay(date);
        DateTime end = DateUtil.endOfDay(date);
        TradeOrderQuery tradeOrderQuery = new TradeOrderQuery().setSkuCode(orderBO.getSkuCode())
                .setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()).setStartCreateTime(start).setEndCreateTime(end);
        List<TradePriceBO> tradePriceBOS = tradeOrderFacade.tradePrice(tradeOrderQuery);

        return TradeConvert.INSTANCE.toTradePriceVO(tradePriceBOS);
    }
}
