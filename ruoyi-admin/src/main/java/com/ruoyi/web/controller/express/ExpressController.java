package com.ruoyi.web.controller.express;


import cn.hutool.core.lang.Assert;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.web.vo.express.DetailInfoVO;
import com.ruoyi.web.vo.express.RouteInfoVO;
import com.ruoyi.web.vo.express.RouteSubscribeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "快递信息相关")
@RequestMapping("/system/company")
public class    ExpressController {

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @ApiOperation("查询快递信息")
    @GetMapping("/info/{logisticsNo}")
    public RouteSubscribeVO info(@PathVariable("logisticsNo") String logisticsNo) {

        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setLogisticsNo(logisticsNo));

        Assert.notNull(routeSubscribeBO, "未查询到快递信息");

        RouteSubscribeVO routeSubscribeVO = new RouteSubscribeVO().setLogisticsNo(routeSubscribeBO.getLogisticsNo())
                .setRouteInfoVO(JacksonUtil.parse(routeSubscribeBO.getRouteInfo(), RouteInfoVO.class)).setDetailInfoVOList(JacksonUtil.parseList(routeSubscribeBO.getDetailInfo(), DetailInfoVO.class));


        return routeSubscribeVO;

    }

}
