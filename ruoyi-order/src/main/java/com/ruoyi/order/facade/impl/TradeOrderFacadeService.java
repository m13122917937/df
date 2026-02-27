package com.ruoyi.order.facade.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.order.convert.TradeOrderCov;
import com.ruoyi.order.domain.TradeOrder;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.manager.TradeOrderManager;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.bo.TradePriceBO;
import com.ruoyi.order.model.param.TradeOrderParam;
import com.ruoyi.order.model.query.TradeOrderQuery;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 成交订单Service接口
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Service
public class TradeOrderFacadeService implements ITradeOrderFacade {

    @Autowired
    private TradeOrderManager tradeOrderManager;

    @Override
    public List<TradeOrderBO> list(TradeOrderQuery query) {
        Wrapper<TradeOrder> wrapper = DynamicCondition.toWrapper(query);
        return TradeOrderCov.INSTANCE.listToBO(tradeOrderManager.list(wrapper));
    }

    @Override
    public TradeOrderBO save(TradeOrderParam param) {
        TradeOrder tradeOrder = TradeOrderCov.INSTANCE.paramToDomain(param);
        tradeOrder.setCreateTime(DateUtil.date()).setUpdateTime(DateUtil.date());
        tradeOrderManager.save(tradeOrder);

        return TradeOrderCov.INSTANCE.toBO(tradeOrder);
    }


    @Override
    public TradeOrderBO getOne(TradeOrderQuery query) {
        Wrapper<TradeOrder> wrapper = DynamicCondition.toWrapper(query);
        return TradeOrderCov.INSTANCE.toBO(tradeOrderManager.getOne(wrapper));
    }

    @Override
    public long count(TradeOrderQuery query) {
        Wrapper<TradeOrder> wrapper = DynamicCondition.toWrapper(query);
        return tradeOrderManager.count(wrapper);
    }

    @Override
    public boolean update(TradeOrderParam param, TradeOrderQuery query) {
        Wrapper<TradeOrder> wrapper = DynamicCondition.toWrapper(query);
        return tradeOrderManager.update(TradeOrderCov.INSTANCE.paramToDomain(param), wrapper);
    }

    @Override
    public List<TradePriceBO> tradePrice(TradeOrderQuery query) {

        List<TradePriceBO> tradePriceBOS = tradeOrderManager.tradePrice(query);
        return tradePriceBOS;
    }

}
