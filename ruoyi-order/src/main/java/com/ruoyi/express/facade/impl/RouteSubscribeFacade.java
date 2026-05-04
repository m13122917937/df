package com.ruoyi.express.facade.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.express.convert.RouteSubscribeCov;
import com.ruoyi.express.domain.RouteSubscribe;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.service.RouteSubscribeService;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.param.RouteSubscribeParam;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.framework.mybatis.DynamicCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 快递信息订阅Service接口
 *
 * @author ruoyi
 * @date 2025-09-25
 */
@Slf4j
@Service
public class RouteSubscribeFacade implements IRouteSubscribeFacade {

    @Autowired
    private RouteSubscribeService routeSubscribeService;

    @Override
    public List<RouteSubscribeBO> list(RouteSubscribeQuery query, SortBy sort) {

        return RouteSubscribeCov.INSTANCE.listToBO(routeSubscribeService.list(DynamicCondition.toWrapper(query, sort)));
    }

    @Override
    public long count(RouteSubscribeQuery query) {
        return routeSubscribeService.count(DynamicCondition.toWrapper(query));
    }

    @Override
    public boolean remove(RouteSubscribeQuery query) {
        return routeSubscribeService.remove(DynamicCondition.toWrapper(query));
    }


    @Override
    public PageBO<RouteSubscribeBO> listPage(final RouteSubscribeQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        Wrapper<RouteSubscribe> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
        return PageUtils.fromList(routeSubscribeService.list(wrapper), RouteSubscribeCov.INSTANCE::listToBO);
    }


    @Override
    public RouteSubscribeBO getOne(RouteSubscribeQuery query) {
        RouteSubscribe routeSubscribe = routeSubscribeService.getOne(DynamicCondition.toWrapper(query));
        return RouteSubscribeCov.INSTANCE.toBO(routeSubscribe);
    }

    @Override
    public boolean update(RouteSubscribeParam param, RouteSubscribeQuery query) {
        return routeSubscribeService.update(RouteSubscribeCov.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }

    @Override
    public RouteSubscribeBO save(RouteSubscribeParam param) {
        RouteSubscribe routeSubscribe = RouteSubscribeCov.INSTANCE.paramToDomain(param);

        routeSubscribeService.save(routeSubscribe);
        return RouteSubscribeCov.INSTANCE.toBO(routeSubscribe);
    }

}
