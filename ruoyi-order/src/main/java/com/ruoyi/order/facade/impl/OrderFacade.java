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
import com.ruoyi.order.service.OrderService;
import com.ruoyi.order.service.OrderSupplierPushService;
import com.ruoyi.order.model.bo.*;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.param.SupplierPushParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.OrderTabCountQuery;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


/**
 * 订单Service接口
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Component
public class OrderFacade implements IOrderFacade  {

    private final OrderService orderService;
    private final OrderSupplierPushService orderSupplierPushService;

    /**
     * 创建订单领域门面。
     *
     * @param orderService 订单服务
     * @param orderSupplierPushService 定向推送事务服务
     */
    public OrderFacade(OrderService orderService, OrderSupplierPushService orderSupplierPushService) {
        this.orderService = orderService;
        this.orderSupplierPushService = orderSupplierPushService;
    }

    @Override
    public List<OrderBO> list(OrderQuery query) {
        Wrapper<Order> wrapper = DynamicCondition.toWrapper(query);
        return OrderCov.INSTANCE.listToBO(orderService.list(wrapper));
    }

    @Override
    public List<OrderBO> list(final OrderQuery query, final SortBy sort) {

        Wrapper<Order> wrapper = DynamicCondition.toWrapper(query, sort);

        return OrderCov.INSTANCE.listToBO(orderService.list(wrapper));
    }

    @Override
    public PageBO<OrderBO> listPage(final OrderQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<Order> list = orderService.list(DynamicCondition.toWrapper(query, pageParam.getSort()));
        return PageUtils.fromList(list, OrderCov.INSTANCE::listToBO);

    }


    @Override
    public OrderBO getOne(OrderQuery query) {
        Order domain = OrderCov.INSTANCE.queryToDomain(query);
        return OrderCov.INSTANCE.toBO(orderService.getOne(new QueryWrapper<>(domain)));
    }

    @Override
    public boolean update(OrderParam param, OrderQuery query) {
        Order queryDomain = OrderCov.INSTANCE.queryToDomain(query);
        Order order = OrderCov.INSTANCE.paramToDomain(param).setUpdateTime(DateUtil.date());
        return orderService.update(order, new QueryWrapper<>(queryDomain));
    }

    @Override
    public Long count(OrderQuery query) {
        long count = orderService.count(DynamicCondition.toWrapper(query));

        return count;
    }

    @Override
    public List<ProvinceCountBO> provinceCount(final OrderQuery orderQuery) {

        List<ProvinceCountDTO> provinceCountDTOS = orderService.getBaseMapper().provinceCount(orderQuery);
        return OrderCov.INSTANCE.toProvinceCount(provinceCountDTOS);

    }

    @Override
    public List<ProvinceCountBO> customerProvinceCount(OrderQuery orderQuery) {
        List<ProvinceCountDTO> provinceCountDTOS = orderService.getBaseMapper().customerProvinceCount(orderQuery);
        return OrderCov.INSTANCE.toProvinceCount(provinceCountDTOS);
    }

    @Override
    public List<BrandCountBO> customerBrandCount(OrderQuery orderQuery) {
        List<BrandCountDTO> provinceCountDTOS = orderService.getBaseMapper().customerBrandCount(orderQuery);
        return OrderCov.INSTANCE.toBrandCount(provinceCountDTOS);
    }

    @Override
    public List<ProvinceCityCountBO> provinceCityCount(OrderTabCountQuery query) {
        List<ProvinceCityCountDTO> provinceCountDTOS = orderService.getBaseMapper().provinceCityCount(query);
        return OrderCov.INSTANCE.toProvinceCityCount(provinceCountDTOS);
    }

    @Override
    public List<BrandCountBO> brandCount(final OrderQuery orderQuery) {
        List<BrandCountDTO> brandCountBOS = orderService.getBaseMapper().brandCount(orderQuery);
        return OrderCov.INSTANCE.toBrandCount(brandCountBOS);

    }

    @Override
    public OrderBO save(final OrderParam param) {
        Order domain = OrderCov.INSTANCE.paramToDomain(param);
        String id = HexUtil.toHex(IdUtil.getSnowflake().nextId());
        domain.setCreateTime(DateUtil.date()).setOrderCode(id);
        orderService.save(domain);
        return OrderCov.INSTANCE.toBO(domain);
    }

    @Override
    public List<OrderStatusDTO> countHeader(DateTime dateTime) {
        return orderService.countHeader(dateTime);
    }

    @Override
    public PageBO<CompanyOrderBO> companyListPage(OrderQuery orderQuery, PageParamV2 pageParamV2) {
        PageUtils.startPage(pageParamV2);

        List<CompanyOrderDTO> companyOrderDTOS = orderService.companyListPage(orderQuery);

        return PageUtils.fromList(companyOrderDTOS, OrderCov.INSTANCE::companyTOBO);
    }

    @Override
    public PageBO<SendOrderLisBO> sendListPage(OrderQuery orderQuery, PageParamV2 pageParamV2) {
        PageUtils.startPage(pageParamV2);
        List<SendOrderLisDTO> companyOrderDTOS = orderService.sendListPage(orderQuery);
        return PageUtils.fromList(companyOrderDTOS, OrderCov.INSTANCE::sendTOBO);
    }

    @Override
    public List<ProductBO> productCount(OrderTabCountQuery query) {
        List<ProductDTO> provinceCountDTOS = orderService.getBaseMapper().productCount(query);
        return OrderCov.INSTANCE.toProductCount(provinceCountDTOS);
    }

    @Override
    public List<SkuBO> skuCount(OrderTabCountQuery query) {
        List<SkuDTO> provinceCountDTOS = orderService.getBaseMapper().skuCount(query);
        return OrderCov.INSTANCE.toSkuCount(provinceCountDTOS);

    }

    /**
     * 批量定向推送供应商。
     *
     * @param param 推送参数
     */
    @Override
    public void pushSupplierBatch(SupplierPushParam param) {
        orderSupplierPushService.pushSupplierBatch(param);
    }

    /**
     * 查询订单中已存在的店铺名称（去重）。
     *
     * @return 店铺名称集合
     */
    @Override
    public List<String> listShopNames() {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT shop_name").isNotNull("shop_name").ne("shop_name", "");
        return orderService.listObjs(wrapper, o -> String.valueOf(o));
    }

}
