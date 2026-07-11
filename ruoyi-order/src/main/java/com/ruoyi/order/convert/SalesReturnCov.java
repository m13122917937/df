package com.ruoyi.order.convert;

import com.ruoyi.order.domain.SalesReturn;
import com.ruoyi.order.model.bo.SalesReturnBO;
import com.ruoyi.order.model.param.SalesReturnParam;
import com.ruoyi.order.model.query.SalesReturnQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SalesReturnCov {

    SalesReturnCov INSTANCE = Mappers.getMapper(SalesReturnCov.class);

    List<SalesReturnBO> listToBO(List<SalesReturn> list);

    SalesReturnBO toBO(SalesReturn domain);

    SalesReturn paramToDomain(SalesReturnParam param);

    SalesReturn queryToDomain(SalesReturnQuery query);
}
