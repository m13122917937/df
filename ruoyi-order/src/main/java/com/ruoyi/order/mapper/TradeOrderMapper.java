package com.ruoyi.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.order.domain.TradeOrder;
import com.ruoyi.order.domain.dto.TradePriceDTO;
import com.ruoyi.order.model.query.TradeOrderQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 成交订单Mapper接口
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Mapper
public interface TradeOrderMapper extends BaseMapper<TradeOrder> {
    /**
     * 查询成交订单
     *
     * @return 成交订单
     */
    List<TradePriceDTO> tradePrice(TradeOrderQuery query);


}
