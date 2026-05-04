package com.ruoyi.order.convert;


import com.ruoyi.order.domain.ImeiSkuRel;
import com.ruoyi.order.model.bo.ImeiSkuRelBO;
import com.ruoyi.order.model.param.ImeiSkuRelParam;
import com.ruoyi.order.model.query.ImeiSkuRelQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ImeiSkuRelCov {

    ImeiSkuRelCov INSTANCE = Mappers.getMapper(ImeiSkuRelCov.class);


    List<ImeiSkuRelBO>  listToBO(List<ImeiSkuRel>  list );

    ImeiSkuRelBO   toBO(ImeiSkuRel  list );

    ImeiSkuRel  queryToDomain(ImeiSkuRelQuery query);

    ImeiSkuRel  paramToDomain(ImeiSkuRelParam param);
}

