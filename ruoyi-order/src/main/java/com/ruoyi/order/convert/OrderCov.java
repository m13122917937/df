package com.ruoyi.order.convert;

import com.ruoyi.order.domain.Order;
import com.ruoyi.order.domain.dto.*;
import com.ruoyi.order.model.bo.*;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderCov {

    OrderCov INSTANCE = Mappers.getMapper(OrderCov.class);


    List<OrderBO> listToBO(List<Order> list);

    OrderBO toBO(Order list);

    Order queryToDomain(OrderQuery query);

    Order paramToDomain(OrderParam param);

    List<ProvinceCountBO> toProvinceCount(List<ProvinceCountDTO> provinceCountDTOS);

    List<BrandCountBO> toBrandCount(List<BrandCountDTO> brandCountBOS);

    List<CompanyOrderBO> companyTOBO(List<CompanyOrderDTO> companyOrderDTOS);

    List<ProvinceCityCountBO> toProvinceCityCount(List<ProvinceCityCountDTO> provinceCountDTOS);

    List<ProductBO> toProductCount(List<ProductDTO> productBOList);

    List<SkuBO> toSkuCount(List<SkuDTO> provinceCountDTOS);

    List<SendOrderLisBO> sendTOBO(List<SendOrderLisDTO> sendOrderLisDTOS);

}

