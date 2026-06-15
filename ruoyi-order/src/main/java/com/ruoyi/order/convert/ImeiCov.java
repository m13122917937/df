package com.ruoyi.order.convert;

import com.ruoyi.order.domain.Imei;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.order.model.query.ImeiQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ImeiCov {

    ImeiCov INSTANCE = Mappers.getMapper(ImeiCov.class);


    List<ImeiBO>   listToBO(List<Imei>  list );

    ImeiBO   toBO(Imei  list );

    Imei queryToDomain(ImeiQuery query);

    Imei  paramToDomain(ImeiParam param);

    List<Imei> paramToDomainList(List<ImeiParam> list);
}

