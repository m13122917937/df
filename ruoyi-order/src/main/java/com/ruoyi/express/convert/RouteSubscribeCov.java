package com.ruoyi.express.convert;


import com.ruoyi.express.domain.RouteSubscribe;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.param.RouteSubscribeParam;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RouteSubscribeCov {

    RouteSubscribeCov INSTANCE = Mappers.getMapper(RouteSubscribeCov.class);


    List<RouteSubscribeBO>  listToBO(List<RouteSubscribe>  list );

    RouteSubscribeBO   toBO(RouteSubscribe  list );

    RouteSubscribe  queryToDomain(RouteSubscribeQuery query);

    RouteSubscribe  paramToDomain(RouteSubscribeParam param);



}

