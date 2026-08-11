package com.ruoyi.subsidy.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.subsidy.domain.GbOrderItem;
import com.ruoyi.subsidy.mapper.GbOrderItemMapper;
import org.springframework.stereotype.Service;

/**
 * 国补订单商品快照领域服务。
 */
@Service
public class GbOrderItemService extends ServiceImpl<GbOrderItemMapper, GbOrderItem> {

    /**
     * 查询订单唯一商品快照。
     *
     * @param orderId 订单ID
     * @return 商品快照
     */
    public GbOrderItem getFirstByOrderId(final Long orderId) {
        return baseMapper.selectFirstByOrderId(orderId);
    }
}
