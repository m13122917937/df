package com.ruoyi.order.service;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.order.domain.Order;
import com.ruoyi.order.domain.dto.CompanyOrderDTO;
import com.ruoyi.order.domain.dto.OrderStatusDTO;
import com.ruoyi.order.domain.dto.SendOrderLisDTO;
import com.ruoyi.order.mapper.OrderMapper;
import com.ruoyi.order.model.query.OrderQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {
    @Autowired
    private OrderMapper orderMapper;

    public List<OrderStatusDTO> countHeader(DateTime dateTime) {
        return orderMapper.countHeader(dateTime);
    }

    public List<CompanyOrderDTO> companyListPage(OrderQuery orderQuery) {

        return this.getBaseMapper().companyListPage(orderQuery);
    }

    public List<SendOrderLisDTO> sendListPage(OrderQuery orderQuery) {

        return this.getBaseMapper().sendListPage(orderQuery);
    }
}
