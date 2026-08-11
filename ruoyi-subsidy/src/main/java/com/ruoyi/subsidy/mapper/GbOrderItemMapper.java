package com.ruoyi.subsidy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.subsidy.domain.GbOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 国补订单商品 Mapper。
 */
@Mapper
public interface GbOrderItemMapper extends BaseMapper<GbOrderItem> {

    /**
     * 查询订单唯一商品快照。
     *
     * @param orderId 订单ID
     * @return 商品快照
     */
    GbOrderItem selectFirstByOrderId(@Param("orderId") Long orderId);
}
