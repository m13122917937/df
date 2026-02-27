package com.ruoyi.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.order.domain.Order;
import com.ruoyi.order.domain.dto.*;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.OrderTabCountQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 订单Mapper接口
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    List<ProvinceCountDTO> provinceCount(OrderQuery orderQuery);

    List<ProvinceCountDTO> customerProvinceCount(OrderQuery orderQuery);


    List<BrandCountDTO> brandCount(OrderQuery orderQuery);

    List<OrderStatusDTO> countHeader(@Param("date") Date date);


    List<CompanyOrderDTO> companyListPage(OrderQuery orderQuery);

    List<ProvinceCityCountDTO> provinceCityCount( OrderTabCountQuery query);

    List<ProductDTO> productCount(OrderTabCountQuery query);

    List<SkuDTO> skuCount(OrderTabCountQuery query);


    List<BrandCountDTO> customerBrandCount(OrderQuery orderQuery);

    List<SendOrderLisDTO> sendListPage(OrderQuery orderQuery);

}
