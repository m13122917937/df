package com.ruoyi.order.convert;

import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.order.domain.Apply;
import com.ruoyi.order.model.bo.ApplyBO;
import com.ruoyi.order.model.query.ApplyQuery;
import com.ruoyi.order.model.param.ApplyParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ApplyCov {

    ApplyCov INSTANCE = Mappers.getMapper(ApplyCov.class);


    List<ApplyBO>  listToBO(List<Apply>  list );

    ApplyBO   toBO(Apply  list );

    Apply  queryToDomain(ApplyQuery query);

    Apply  paramToDomain(ApplyParam param);
}

