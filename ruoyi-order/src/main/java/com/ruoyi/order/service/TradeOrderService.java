package com.ruoyi.order.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.order.convert.TradeOrderCov;
import com.ruoyi.order.domain.TradeOrder;
import com.ruoyi.order.domain.dto.TradePriceDTO;
import com.ruoyi.order.mapper.TradeOrderMapper;
import com.ruoyi.order.model.bo.TradePriceBO;
import com.ruoyi.order.model.query.TradeOrderQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 成交订单Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Service
public class TradeOrderService  extends ServiceImpl<TradeOrderMapper, TradeOrder> {
    @Autowired
    private TradeOrderMapper tradeOrderMapper;

    public List<TradePriceBO> tradePrice(TradeOrderQuery query) {
        List<TradePriceDTO> tradePriceDTOList = tradeOrderMapper.tradePrice(query);
        return TradeOrderCov.INSTANCE.toTradePriceBO(tradePriceDTOList);

    }
}
