package com.ruoyi.express.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.param.RouteSubscribeParam;
import com.ruoyi.express.model.query.RouteSubscribeQuery;

import java.util.List;

/**
 * 快递信息订阅Service接口
 *
 * @author ruoyi
 * @date 2025-09-25
 */
public interface IRouteSubscribeFacade {

    List<RouteSubscribeBO> list(RouteSubscribeQuery query, SortBy sort);

    long count(RouteSubscribeQuery query);

    boolean remove(RouteSubscribeQuery query);

    PageBO<RouteSubscribeBO> listPage(RouteSubscribeQuery query, PageParamV2 pageParam);

    RouteSubscribeBO getOne(RouteSubscribeQuery query);

    boolean update(RouteSubscribeParam param, RouteSubscribeQuery query);

    RouteSubscribeBO save(RouteSubscribeParam param);

}
