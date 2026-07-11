package com.ruoyi.mapper.jky;

import com.ruoyi.jky.param.logistics.LogisticsUpdateParam;
import com.ruoyi.order.model.bo.JkyLogisticsTaskBO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JkyLogisticsConvert {

    JkyLogisticsConvert INSTANCE = Mappers.getMapper(JkyLogisticsConvert.class);

    @Mapping(target = "orderNo", expression = "java(task.getErpOrderId() != null ? task.getErpOrderId() : task.getOrderCode())")
    @Mapping(target = "logisticNo", source = "logisticsNo")
    @Mapping(target = "logisticName", source = "logisticsName")
    @Mapping(target = "logisticCode", source = "logisticsCode")
    LogisticsUpdateParam.LogisticsUpdateItem toItem(JkyLogisticsTaskBO task);

}