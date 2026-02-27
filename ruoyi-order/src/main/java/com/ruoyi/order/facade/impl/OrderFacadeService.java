package com.ruoyi.order.facade.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.order.convert.OrderCov;
import com.ruoyi.order.domain.Order;
import com.ruoyi.order.domain.dto.*;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.manager.OrderManager;
import com.ruoyi.order.model.bo.*;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.OrderTabCountQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * 订单Service接口
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Service
public class OrderFacadeService implements IOrderFacade  {

    @Autowired
    private OrderManager orderManager;

    @Override
    public List<OrderBO> list(OrderQuery query) {
        Wrapper<Order> wrapper = DynamicCondition.toWrapper(query);
        return OrderCov.INSTANCE.listToBO(orderManager.list(wrapper));
    }

    @Override
    public List<OrderBO> list(final OrderQuery query, final SortBy sort) {

        Wrapper<Order> wrapper = DynamicCondition.toWrapper(query, sort);

        return OrderCov.INSTANCE.listToBO(orderManager.list(wrapper));
    }

    @Override
    public PageBO<OrderBO> listPage(final OrderQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<Order> list = orderManager.list(DynamicCondition.toWrapper(query, pageParam.getSort()));
        return PageUtils.fromList(list, OrderCov.INSTANCE::listToBO);

    }

    @Override
    public OrderBO getOne(OrderQuery query) {
        Order domain = OrderCov.INSTANCE.queryToDomain(query);
        return OrderCov.INSTANCE.toBO(orderManager.getOne(new QueryWrapper<>(domain)));
    }

    @Override
    public boolean update(OrderParam param, OrderQuery query) {
        Order queryDomain = OrderCov.INSTANCE.queryToDomain(query);
        Order order = OrderCov.INSTANCE.paramToDomain(param).setUpdateTime(DateUtil.date());
        return orderManager.update(order, new QueryWrapper<>(queryDomain));
    }

    @Override
    public Long count(OrderQuery query) {
        long count = orderManager.count(DynamicCondition.toWrapper(query));

        return count;
    }

    @Override
    public List<ProvinceCountBO> provinceCount(final OrderQuery orderQuery) {

        List<ProvinceCountDTO> provinceCountDTOS = orderManager.getBaseMapper().provinceCount(orderQuery);
        return OrderCov.INSTANCE.toProvinceCount(provinceCountDTOS);

    }

    @Override
    public List<ProvinceCountBO> customerProvinceCount(OrderQuery orderQuery) {
        List<ProvinceCountDTO> provinceCountDTOS = orderManager.getBaseMapper().customerProvinceCount(orderQuery);
        return OrderCov.INSTANCE.toProvinceCount(provinceCountDTOS);
    }

    @Override
    public List<BrandCountBO> customerBrandCount(OrderQuery orderQuery) {
        List<BrandCountDTO> provinceCountDTOS = orderManager.getBaseMapper().customerBrandCount(orderQuery);
        return OrderCov.INSTANCE.toBrandCount(provinceCountDTOS);
    }

    @Override
    public List<ProvinceCityCountBO> provinceCityCount(OrderTabCountQuery query) {
        List<ProvinceCityCountDTO> provinceCountDTOS = orderManager.getBaseMapper().provinceCityCount(query);
        return OrderCov.INSTANCE.toProvinceCityCount(provinceCountDTOS);
    }

    @Override
    public List<BrandCountBO> brandCount(final OrderQuery orderQuery) {
        List<BrandCountDTO> brandCountBOS = orderManager.getBaseMapper().brandCount(orderQuery);
        return OrderCov.INSTANCE.toBrandCount(brandCountBOS);

    }

    @Override
    public OrderBO save(final OrderParam param) {
        Order domain = OrderCov.INSTANCE.paramToDomain(param);
        String id = HexUtil.toHex(IdUtil.getSnowflake().nextId());
        domain.setCreateTime(DateUtil.date()).setOrderCode(id);
        orderManager.save(domain);
        return OrderCov.INSTANCE.toBO(domain);
    }

    @Override
    public List<OrderStatusDTO> countHeader(DateTime dateTime) {
        return orderManager.countHeader(dateTime);
    }

    @Override
    public PageBO<CompanyOrderBO> companyListPage(OrderQuery orderQuery, PageParamV2 pageParamV2) {
        PageUtils.startPage(pageParamV2);

        List<CompanyOrderDTO> companyOrderDTOS = orderManager.companyListPage(orderQuery);

        return PageUtils.fromList(companyOrderDTOS, OrderCov.INSTANCE::companyTOBO);
    }

    @Override
    public PageBO<SendOrderLisBO> sendListPage(OrderQuery orderQuery, PageParamV2 pageParamV2) {
        PageUtils.startPage(pageParamV2);
        List<SendOrderLisDTO> companyOrderDTOS = orderManager.sendListPage(orderQuery);
        return PageUtils.fromList(companyOrderDTOS, OrderCov.INSTANCE::sendTOBO);
    }

    @Override
    public List<ProductBO> productCount(OrderTabCountQuery query) {
        List<ProductDTO> provinceCountDTOS = orderManager.getBaseMapper().productCount(query);
        return OrderCov.INSTANCE.toProductCount(provinceCountDTOS);
    }

    @Override
    public List<SkuBO> skuCount(OrderTabCountQuery query) {
        List<SkuDTO> provinceCountDTOS = orderManager.getBaseMapper().skuCount(query);
        return OrderCov.INSTANCE.toSkuCount(provinceCountDTOS);

    }


}
