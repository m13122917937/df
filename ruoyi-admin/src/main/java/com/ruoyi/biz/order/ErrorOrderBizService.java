package com.ruoyi.biz.order;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.express.JkyStockInAndDeliveryBizService;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.ImeiConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.ImeiQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.web.form.order.ExcelForm;
import com.ruoyi.web.vo.order.ExcelPlatformVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ErrorOrderBizService {

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    IImeiFacade imeiFacade;

    @Autowired
    JkyStockInAndDeliveryBizService jkyStockInAndDeliveryBizService;

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    /**
     * 订单列表
     *
     * @param excelForm
     * @return
     */
    public List<ExcelPlatformVO> listInfo(final ExcelForm excelForm) {

        DateTime dateTime = DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -15));
        List<OrderBO> orderBOList = orderFacade.list(new OrderQuery().setStatus(OrderConsts.OrderStatus.DELIVERY_ING.getCode()).setCreateTimeStart(dateTime)
                .setCategoryList(excelForm.getCategoryList()).setBrandSet(excelForm.getBrandSet()));
        if (CollectionUtil.isEmpty(orderBOList)) {
            return Collections.EMPTY_LIST;
        }
        List<ExcelPlatformVO> list = new ArrayList();
        for (OrderBO orderBO : orderBOList) {
            List<ImeiBO> imeiBOS = imeiFacade.list(new ImeiQuery().setOrderId(orderBO.getOrderCode()).setActivated(ImeiConsts.Activated.SUCCESS.getCode()).setPlatformImei(ImeiConsts.PlatformImei.WAIT_QUERY.getCode()));
            if (CollectionUtil.isEmpty(imeiBOS)) {
                continue;
            }

            for (ImeiBO imeiBO : imeiBOS) {

                ExcelPlatformVO excelPlatformVO = new ExcelPlatformVO();
                excelPlatformVO.setOrderCode(orderBO.getOrderCode());
                excelPlatformVO.setOriginalOrderId(orderBO.getOriginalOrderId());
                excelPlatformVO.setImei(imeiBO.getImel());
                excelPlatformVO.setSn(imeiBO.getSn());
                excelPlatformVO.setPlatform(orderBO.getPlatform());
                excelPlatformVO.setCategory(orderBO.getCategory());
                excelPlatformVO.setBrand(orderBO.getBrand());
                list.add(excelPlatformVO);
            }
        }
        return list;
    }

    /**
     * 发货中订单转当日发货
     *
     * @param orderCodeList 订单号列表
     */
    @Transactional
    public void deliveryIngToToday(final List<String> orderCodeList) {
        for (String orderCode : orderCodeList) {
            deliveryIngToToday(orderCode);
        }
    }

    private void deliveryIngToToday(final String orderCode) {
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        Assert.notNull(orderBO, "订单不存在");
        Assert.isTrue(Objects.equals(orderBO.getStatus(), OrderConsts.OrderStatus.DELIVERY_ING.getCode()), "只有发货中订单可以转当日发货");
        imeiFacade.update(new ImeiParam().setPlatformImei(ImeiConsts.PlatformImei.NORMAL.getCode()).setPlatformTime(DateUtil.date()),
                new ImeiQuery().setOrderId(orderCode));
        orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.DELIVERY_END.getCode()).setUpdateTime(DateUtil.date()),
                new OrderQuery().setOrderCode(orderCode));

        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setOrderCode(orderCode));
        jkyStockInAndDeliveryBizService.createJkyStockIn(orderBO, routeSubscribeBO);
    }

}
