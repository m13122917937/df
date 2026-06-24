package com.ruoyi.biz.excel;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.biz.express.JkyStockInAndDeliveryBizService;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.ImeiConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.ImeiQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.web.vo.order.ExcelPlatformVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class ExcelPlateformReadListener implements ReadListener<ExcelPlatformVO> {

    IImeiFacade imeiFacade;

    IOrderFacade orderFacade;

    JkyStockInAndDeliveryBizService jkyStockInAndDeliveryBizService;

    IRouteSubscribeFacade routeSubscribeFacade;


    public ExcelPlateformReadListener(IImeiFacade imeiFacade, IOrderFacade orderFacade,
                                      JkyStockInAndDeliveryBizService jkyStockInAndDeliveryBizService,
                                      IRouteSubscribeFacade routeSubscribeFacade) {
        this.imeiFacade = imeiFacade;
        this.orderFacade = orderFacade;
        this.jkyStockInAndDeliveryBizService = jkyStockInAndDeliveryBizService;
        this.routeSubscribeFacade = routeSubscribeFacade;
    }

    @Override
    public void invoke(ExcelPlatformVO excelPlatformVO, AnalysisContext analysisContext) {
        log.info("excelPlatformVO:{}", excelPlatformVO);
        ImeiConsts.PlatformImei platformImei = ImeiConsts.PlatformImei.getByName(excelPlatformVO.getValidated());
        if (Objects.isNull(platformImei)) {
            return;
        }
        if (StrUtil.isBlank(excelPlatformVO.getOrderCode())) {
            return;
        }
        //
        boolean update = imeiFacade.update(new ImeiParam().setPlatformImei(platformImei.getCode()).setPlatformTime(DateUtil.date()), new ImeiQuery()
                .setOrderId(excelPlatformVO.getOrderCode()).setSn(excelPlatformVO.getSn()).setImel(excelPlatformVO.getImei()));
        if (!update) {
            return;
        }
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(excelPlatformVO.getOrderCode()));

        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setOrderCode(orderBO.getOrderCode()));

        // 修改订单状态
        if (Objects.equals(platformImei.getCode(), ImeiConsts.PlatformImei.NORMAL.getCode())) {
            if (Objects.nonNull(routeSubscribeBO)) {
                orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.DELIVERY_END.getCode()),
                        new OrderQuery().setOrderCode(excelPlatformVO.getOrderCode()));

                jkyStockInAndDeliveryBizService.createJkyStockIn(orderBO, routeSubscribeBO);
            } else {
                log.info("待填写物流单号：{}", orderBO.getOrderCode());
                orderFacade.update(new OrderParam().setSubStatus(OrderConsts.OrderSubStatus.WAIT_EXPRESS.getCode()),
                        new OrderQuery().setOrderCode(excelPlatformVO.getOrderCode()));
            }
        } else {
            orderFacade.update(new OrderParam().setSubStatus(OrderConsts.OrderSubStatus.WAIT_SALES_ERROR.getCode()), new OrderQuery().setOrderCode(excelPlatformVO.getOrderCode()));
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
