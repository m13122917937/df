package com.ruoyi.order.convert;

import com.ruoyi.order.domain.HangingOrder;
import com.ruoyi.order.model.bo.HangingOrderBO;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface HangingOrderCov {

    HangingOrderCov INSTANCE = Mappers.getMapper(HangingOrderCov.class);


    List<HangingOrderBO>   listToBO(List<HangingOrder>  list );

    HangingOrderBO toBO(HangingOrder  list );

    HangingOrder queryToDomain(HangingOrderQuery query);

    HangingOrder  paramToDomain(HangingOrderParam param);
}

