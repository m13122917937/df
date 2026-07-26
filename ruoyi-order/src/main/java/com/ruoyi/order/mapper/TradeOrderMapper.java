package com.ruoyi.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.order.domain.TradeOrder;
import com.ruoyi.order.domain.dto.TradePriceDTO;
import com.ruoyi.order.model.query.TradeOrderQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     * 查询并锁定订单当前有效成交记录。
     *
     * @param orderCodes 订单编号
     * @param status 有效状态
     * @return 有效成交记录
     */
    List<TradeOrder> selectActiveByOrderCodesForUpdate(@Param("orderCodes") List<String> orderCodes,
                                                       @Param("status") Integer status);

    /**
     * 查询成交订单
     *
     * @return 成交订单
     */
    List<TradePriceDTO> tradePrice(TradeOrderQuery query);


}
