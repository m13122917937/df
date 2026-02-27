package com.ruoyi.mapper.order;

import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.order.model.bo.BrandCountBO;
import com.ruoyi.order.model.bo.CompanyOrderBO;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.bo.ProvinceCountBO;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.web.form.order.AllOrderForm;
import com.ruoyi.web.form.order.OrderForm;
import com.ruoyi.web.vo.order.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderConvert {

    OrderConvert INSTANCE = Mappers.getMapper(OrderConvert.class);


    List<OrderVO> toOrderVO(List<CompanyOrderBO> data);

    @Mapping(target = "tradeTime", source = "createTime")
    OrderVO toOrderVOB(CompanyOrderBO data);


    List<OrderAreaCountVO> toOrderAreaCountVO(List<ProvinceCountBO> provinceCountBOS);


    List<OrderBrandCountVO> toOrderBrandCountVO(List<BrandCountBO> provinceCountBOS);

    OrderQuery toOrderQuery(OrderForm orderForm);

    OrderQuery allToOrderQuery(AllOrderForm allOrderForm);

    List<AllOrderVO> toALLOrderVO(List<CompanyOrderBO> data);


    OrderExpressVO toRouteVO(RouteSubscribeBO routeSubscribeBO);


    List<DeliveryEndOrderVO> toDeliveryEndExport(List<OrderVO> orderVOS);

    List<DeliveryEndOrderVO> toDeliveryEndOrderVO(List<CompanyOrderBO> data);


}
