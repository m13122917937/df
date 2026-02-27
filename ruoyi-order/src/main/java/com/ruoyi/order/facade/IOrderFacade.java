package com.ruoyi.order.facade;

import cn.hutool.core.date.DateTime;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.order.domain.dto.OrderStatusDTO;
import com.ruoyi.order.model.bo.*;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.OrderTabCountQuery;

import java.util.Date;
import java.util.List;

/**
 * 订单Service接口
 *
 * @author ruoyi
 * @date 2025-09-09
 */
public interface IOrderFacade {

    // 抽象方法

    List<OrderBO> list(OrderQuery query);

    List<OrderBO> list(OrderQuery query, SortBy sort);

    PageBO<OrderBO> listPage(OrderQuery query, PageParamV2 pageParamV2);

    OrderBO getOne(OrderQuery query);

    boolean update(OrderParam param, OrderQuery query);

    Long count(OrderQuery query);

    OrderBO save(OrderParam param);

    // 自定义方法

    List<ProvinceCountBO> provinceCount(OrderQuery orderQuery);

    List<ProvinceCountBO> customerProvinceCount(OrderQuery orderQuery);

    List<BrandCountBO> customerBrandCount(OrderQuery orderQuery);

    List<ProvinceCityCountBO> provinceCityCount(OrderTabCountQuery query);

    List<BrandCountBO> brandCount(OrderQuery orderQuery);


    List<OrderStatusDTO> countHeader(DateTime dateTime);

    PageBO<CompanyOrderBO> companyListPage(OrderQuery orderQuery, PageParamV2 pageParamV2);

    PageBO<SendOrderLisBO> sendListPage(OrderQuery orderQuery, PageParamV2 pageParamV2);

    List<ProductBO> productCount(OrderTabCountQuery orderTabCountQuery);

    List<SkuBO> skuCount(OrderTabCountQuery orderTabCountQuery);


}
