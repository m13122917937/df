package com.ruoyi.biz.order;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.alibaba.excel.EasyExcel;
import com.ruoyi.biz.common.IDictDistrictBizService;
import com.ruoyi.biz.excel.DeliveryListener;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.consts.LogisticsCode;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.mapper.order.OrderConvert;
import com.ruoyi.order.facade.*;
import com.ruoyi.order.model.bo.*;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.ImeiQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.system.model.bo.DictDistrictBO;
import com.ruoyi.web.form.order.AllOrderForm;
import com.ruoyi.web.form.order.OrderForm;
import com.ruoyi.web.vo.order.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderBizService {

    @Autowired
    IApplyFacade applyFacade;

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    @Autowired
    ITradeOrderFacade tradeFacade;

    @Autowired
    IImeiFacade imeiFacade;

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @Autowired
    IDictDistrictBizService dictDistrictBizService;


    /**
     * 查询订单信息
     *
     * @param pageParamV2
     * @return
     */
    public PageBO<OrderVO> pageList(OrderForm orderForm, PageParamV2 pageParamV2) {
        OrderQuery orderQuery = OrderConvert.INSTANCE.toOrderQuery(orderForm);
        orderQuery.setCreateTime(DateUtil.offsetDay(DateUtil.date(), -15));
        PageBO<CompanyOrderBO> companyOrderBOPageBO = orderFacade.companyListPage(orderQuery, pageParamV2);
        List<OrderVO> orderVOList = OrderConvert.INSTANCE.toOrderVO(companyOrderBOPageBO.getData());
        // 完善省市
        Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
        for (OrderVO orderVO : orderVOList) {
            orderVO.setProvinceName(provinceMap.get(orderVO.getProvince()));
            Map<Long, String> cityMap = dictDistrictBizService.listCity(orderVO.getProvince()).stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
            orderVO.setCityName(cityMap.get(orderVO.getCity()));
            // 挂单信息
            HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderVO.getOrderCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
            orderVO.setDeliveryTime(hangingOrderBO.getDeliveryTime()).setAccountingPeriod(hangingOrderBO.getAccountingPeriod()).setOtherRequire(hangingOrderBO.getOtherRequire())
                    .setCodeOptions(hangingOrderBO.getCodeOptions());
        }
        return new PageBO<OrderVO>(orderVOList, companyOrderBOPageBO.getTotal());

    }


    /**
     * 查询订单信息
     *
     * @return
     */
    public List<DeliveryEndOrderVO> deliveryEndExport(OrderForm orderForm) {
        OrderQuery orderQuery = OrderConvert.INSTANCE.toOrderQuery(orderForm);
        orderQuery.setCreateTime(DateUtil.offsetDay(DateUtil.date(), -15));
        PageBO<CompanyOrderBO> companyOrderBOPageBO = orderFacade.companyListPage(orderQuery, null);
        List<DeliveryEndOrderVO> orderVOList = OrderConvert.INSTANCE.toDeliveryEndOrderVO(companyOrderBOPageBO.getData());
        // 完善省市
        for (DeliveryEndOrderVO orderVO : orderVOList) {
            RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setOrderCode(orderVO.getOrderCode()));
            orderVO.setTrackingNumber(Objects.isNull(routeSubscribeBO) ? null : routeSubscribeBO.getLogisticsNo());
            ImeiBO imeiBO = imeiFacade.getOne(new ImeiQuery().setOrderId(orderVO.getOrderCode()));
            orderVO.setImei(Objects.isNull(imeiBO) ? null : imeiBO.getImel());
            orderVO.setSn(Objects.isNull(imeiBO) ? null : imeiBO.getSn());
        }
        return orderVOList;

    }

    public List<OrderAreaCountVO> provinceCount(Integer status, Long deptId, String brand) {
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);
        List<ProvinceCountBO> provinceCountBOS = orderFacade.customerProvinceCount(new OrderQuery().setStatus(status).setBrand(brand).setCompanyId(deptId).setCreateTime(dateTime));
        List<OrderAreaCountVO> list = OrderConvert.INSTANCE.toOrderAreaCountVO(provinceCountBOS);
        for (OrderAreaCountVO provinceCount : list) {
            Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
            provinceCount.setProvinceName(provinceMap.get(provinceCount.getProvince()));
        }
        return list;
    }

    public List<OrderBrandCountVO> brandCount(Integer status, Long deptId, Long province) {
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);
        List<BrandCountBO> provinceCountBOS = orderFacade.customerBrandCount(new OrderQuery().setStatus(status).setProvince(province).setCompanyId(deptId).setCreateTime(dateTime));
        return OrderConvert.INSTANCE.toOrderBrandCountVO(provinceCountBOS);
    }

    public PageBO<AllOrderVO> allOrderPage(AllOrderForm allOrderForm, PageParamV2 pageParamV2) {
        OrderQuery orderQuery = OrderConvert.INSTANCE.allToOrderQuery(allOrderForm);

        PageBO<CompanyOrderBO> companyOrderBOPageBO = orderFacade.companyListPage(orderQuery, pageParamV2);
        List<AllOrderVO> orderVOList = OrderConvert.INSTANCE.toALLOrderVO(companyOrderBOPageBO.getData());
        // 完善省市
        Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
        for (AllOrderVO orderVO : orderVOList) {
            orderVO.setProvinceName(provinceMap.get(orderVO.getProvince()));
            Map<Long, String> cityMap = dictDistrictBizService.listCity(orderVO.getProvince()).stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
            orderVO.setCityName(cityMap.get(orderVO.getCity()));
        }
        return new PageBO<AllOrderVO>(orderVOList, companyOrderBOPageBO.getTotal());

    }

    /**
     * 确认追单
     *
     * @param orderCode
     */
    public void confirmRevoke(String orderCode) {
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        Assert.notNull(orderBO, "订单不存在");
        Assert.isTrue(Objects.equals(orderBO.getStatus(), OrderConsts.OrderStatus.CHASE_ORDER.getCode()), "订单状态异常，请刷新页面");

        Integer subCode = null;
        if (Objects.equals(orderBO.getSubStatus(), OrderConsts.OrderSubStatus.DELIVERY_ING_BACK.getCode())) {
            subCode = OrderConsts.OrderSubStatus.DELIVERY_ING_BACK_CONF.getCode();
        } else if (Objects.equals(orderBO.getSubStatus(), OrderConsts.OrderSubStatus.DELIVERY_END_BACK.getCode())) {
            subCode = OrderConsts.OrderSubStatus.DELIVERY_END_BACK_CONF.getCode();
        } else {
            return;
        }
        orderFacade.update(new OrderParam().setSubStatus(subCode).setUpdateTime(DateUtil.date()), new OrderQuery().setOrderCode(orderCode));
    }

    public void defaultExpress(PageBO<OrderVO> pageBO) {

        for (OrderVO datum : pageBO.getData()) {
            if (datum.getExpress() == null) {
                datum.setExpress(LogisticsCode.SHUNFENG.getMsg());
            }
        }

    }


}
