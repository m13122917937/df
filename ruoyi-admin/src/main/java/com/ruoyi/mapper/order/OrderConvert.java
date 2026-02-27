package com.ruoyi.mapper.order;

import com.ruoyi.order.domain.dto.OrderStatusDTO;
import com.ruoyi.order.model.bo.*;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.web.form.order.AllOrderForm;
import com.ruoyi.web.form.order.OrderAddForm;
import com.ruoyi.web.form.order.OrderNewForm;
import com.ruoyi.web.vo.order.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderConvert {

    OrderConvert INSTANCE = Mappers.getMapper(OrderConvert.class);

    List<BrandCountVO> toBrandCountVO(List<BrandCountBO> brandCountBOS);

    List<OrderAreaCountVO> toOrderAreaCountVO(List<ProvinceCountBO> provinceCountBOS);


    List<OrderListVO> toWaitVOList(List<OrderBO> list);

    OrderParam toParam(OrderAddForm orderAddForm);

    OrderQuery paramToQuery(OrderNewForm orderNewParam);

    List<OrderStatusVO> toOrderStatus(List<OrderStatusDTO> orderStatusDTOS);

    List<OrderDeliveryVO> toOrderDeliveryVO(List<OrderBO> list);


    OrderQuery allParamToQuery(AllOrderForm allOrderForm);


    List<AllOrderVO> toAllOrderVOList(List<OrderBO> data);


    List<OrderListVO> toOrderVOList(List<SendOrderLisBO> data);

}
