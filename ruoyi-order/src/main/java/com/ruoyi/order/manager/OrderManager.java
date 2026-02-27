package com.ruoyi.order.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.order.domain.Order;
import com.ruoyi.order.domain.Rule;
import com.ruoyi.order.domain.dto.CompanyOrderDTO;
import com.ruoyi.order.domain.dto.OrderStatusDTO;
import com.ruoyi.order.domain.dto.SendOrderLisDTO;
import com.ruoyi.order.mapper.OrderMapper;
import com.ruoyi.order.model.query.OrderQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Service
public class OrderManager extends ServiceImpl<OrderMapper, Order> {
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
