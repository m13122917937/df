package com.ruoyi.web.controller.express;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.biz.express.ExpressBizService;
import com.ruoyi.biz.express.bean.ExpressUtils;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.web.form.ExpressOrderForm;
import com.ruoyi.web.vo.express.DetailInfoVO;
import com.ruoyi.web.vo.express.RouteInfoVO;
import com.ruoyi.web.vo.express.RouteSubscribeVO;
import com.ruoyi.web.vo.order.OrderExpressVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "快递信息相关")
@RestController
@RequestMapping("/express/company")
public class ExpressController {

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @Autowired
    ExpressBizService expressBizService;

    @ApiOperation("查询快递信息")
    @GetMapping("/info/{logisticsNo}")
    public RouteSubscribeVO info(@PathVariable("logisticsNo") String logisticsNo) {

        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setLogisticsNo(logisticsNo));

        Assert.notNull(routeSubscribeBO, "未查询到快递信息");


        RouteSubscribeVO routeSubscribeVO = new RouteSubscribeVO().setLogisticsNo(routeSubscribeBO.getLogisticsNo());
        if (StrUtil.isNotBlank(routeSubscribeBO.getRouteInfo())) {
            routeSubscribeVO.setRouteInfoVO(JacksonUtil.parse(routeSubscribeBO.getRouteInfo(), RouteInfoVO.class));
        }
        if (StrUtil.isNotBlank(routeSubscribeBO.getDetailInfo())) {
            routeSubscribeVO.setDetailInfoVOList(JacksonUtil.parseList(routeSubscribeBO.getDetailInfo(), DetailInfoVO.class));
        }
        if (StrUtil.isBlank(routeSubscribeBO.getRouteInfo())) {
            routeSubscribeVO.setDetailInfoVOList(List.of(ExpressUtils.defaultItem(routeSubscribeBO.getCreateTime())));
        }
        return routeSubscribeVO;

    }

    @ApiOperation("查询快递公司编码")
    @GetMapping("/{orderCode}")
    public AjaxResult listCode(@PathVariable("orderCode") String orderCode) {

        Map<String, String> expressInfo = expressBizService.queryExpressInfo(orderCode);

        return AjaxResult.success(expressInfo);
    }

    @ApiOperation("保存物流单号,并且订阅快递信息")
    @PostMapping("/order")
    public AjaxResult saveOrder(@Validated @RequestBody ExpressOrderForm expressOrderForm) throws IOException {

        expressBizService.saveExpress(expressOrderForm);

        return AjaxResult.success();
    }

    /**
     * 查询快递订单
     *
     * @param orderCode
     * @return
     */
    @ApiOperation("查询订单快递单号")
    @GetMapping("express/{orderCode}")
    public AjaxResult queryExpressInfo(@PathVariable("orderCode") String orderCode) {

        OrderExpressVO orderExpressVO = expressBizService.queryOrderRoute(orderCode);

        return AjaxResult.success(orderExpressVO);
    }

}
