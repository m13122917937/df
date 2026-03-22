package com.ruoyi.web.controller.express;


import cn.hutool.core.lang.Assert;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.web.vo.express.DetailInfoVO;
import com.ruoyi.web.vo.express.RouteInfoVO;
import com.ruoyi.web.vo.express.RouteSubscribeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("/system/company")
public class    ExpressController {

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;


    @GetMapping("/info/{logisticsNo}")
    public RouteSubscribeVO info(@PathVariable("logisticsNo") String logisticsNo) {

        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setLogisticsNo(logisticsNo));

        Assert.notNull(routeSubscribeBO, "未查询到快递信息");

        RouteSubscribeVO routeSubscribeVO = new RouteSubscribeVO().setLogisticsNo(routeSubscribeBO.getLogisticsNo())
                .setRouteInfoVO(JacksonUtil.parse(routeSubscribeBO.getRouteInfo(), RouteInfoVO.class)).setDetailInfoVOList(JacksonUtil.parseList(routeSubscribeBO.getDetailInfo(), DetailInfoVO.class));


        return routeSubscribeVO;

    }

}
