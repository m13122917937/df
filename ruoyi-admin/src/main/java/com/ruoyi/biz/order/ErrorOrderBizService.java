package com.ruoyi.biz.order;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.biz.sys.IDictDistrictBizService;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.kuaidi100.properties.ExpressProperties;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ErrorOrderBizService {

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    @Autowired
    IDictDistrictBizService dictDistrictBizService;

    @Autowired
    RuleBizService ruleBizService;

    @Autowired
    IImeiFacade imeiFacade;

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @Autowired
    ExpressProperties expressProperties;

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

}
