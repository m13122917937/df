package com.ruoyi.order.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.bo.TradePriceBO;
import com.ruoyi.order.model.param.TradeOrderParam;
import com.ruoyi.order.model.query.TradeOrderQuery;

import java.util.List;

/**
 * 成交订单Service接口
 *
 * @author ruoyi
 * @date 2025-09-09
 */
public interface ITradeOrderFacade {

    List<TradeOrderBO> list(TradeOrderQuery query);

    TradeOrderBO save(TradeOrderParam param);

    TradeOrderBO getOne(TradeOrderQuery query);

    long count(TradeOrderQuery query);

    boolean update(TradeOrderParam param, TradeOrderQuery query);

    List<TradePriceBO> tradePrice(TradeOrderQuery query);
}
