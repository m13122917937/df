package com.ruoyi.order.convert;

import com.ruoyi.order.domain.TradeOrder;
import com.ruoyi.order.domain.dto.TradePriceDTO;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.bo.TradePriceBO;
import com.ruoyi.order.model.param.TradeOrderParam;
import com.ruoyi.order.model.query.TradeOrderQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TradeOrderCov {

    TradeOrderCov INSTANCE = Mappers.getMapper(TradeOrderCov.class);


    List<TradeOrderBO> listToBO(List<TradeOrder> list);

    TradeOrderBO toBO(TradeOrder list);

    TradeOrder queryToDomain(TradeOrderQuery query);

    TradeOrder paramToDomain(TradeOrderParam param);

    List<TradePriceBO> toTradePriceBO(List<TradePriceDTO> tradePriceDTO);

}

