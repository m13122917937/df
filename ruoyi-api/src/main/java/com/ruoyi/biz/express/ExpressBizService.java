package com.ruoyi.biz.express;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.biz.company.CompanyCapitalBizService;
import com.ruoyi.biz.express.bean.ExpressCallbackStatusCode;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.capital.model.consts.CompanyCapitalConsts;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.consts.WebConstants;
import com.ruoyi.express.facade.IExpressContrastFacade;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.ExpressContrastBO;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.consts.LogisticsCode;
import com.ruoyi.express.model.consts.RouteSubscribeConsts;
import com.ruoyi.express.model.param.RouteSubscribeParam;
import com.ruoyi.express.model.query.ExpressContrastQuery;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.kuaidi100.ExpressClient;
import com.ruoyi.kuaidi100.model.*;
import com.ruoyi.kuaidi100.model.consts.LogisticsStatus;
import com.ruoyi.kuaidi100.model.reponse.Kd100SubscribeResp;
import com.ruoyi.mapper.order.OrderConvert;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.param.TradeOrderParam;
import com.ruoyi.order.model.query.ImeiQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.system.model.consts.DictDataConsts;
import com.ruoyi.wangdian.param.stock.StockInInfoGoodsList;
import com.ruoyi.wangdian.param.stock.StockInInfoParam;
import com.ruoyi.wangdian.utils.WdtClient;
import com.ruoyi.web.form.ExpressOrderForm;
import com.ruoyi.web.vo.order.OrderExpressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExpressBizService {

    @Autowired
    RuoYiConfig ruoYiConfig;

    @Autowired
    ExpressClient expressClient;

    @Autowired
    WdtClient wdtClient;

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    @Autowired
    IImeiFacade iMeiFacade;

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @Autowired
    IExpressContrastFacade expressContrastFacade;
    @Autowired
    ICompanyCapitalFacade companyCapitalFacade;

    @Autowired
    CompanyCapitalBizService companyCapitalBizService;

    /**
     * 查询快递公司编码
     *
     * @param orderCode
     * @return
     */
    public Map<String, String> queryExpressInfo(String orderCode) {

        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        Assert.notNull(orderBO, "订单不存在");

        Map<String, String> expressInfoMap = new HashMap<>();
        ExpressContrastBO expressContrast = expressContrastFacade.getOne(new ExpressContrastQuery().setBrand(orderBO.getBrand()).setCategory(orderBO.getCategory()));
        // 不存在增加默认发货方式
        if (Objects.isNull(expressContrast)) {
            expressInfoMap.put(LogisticsCode.SHUNFENG.getCode(), LogisticsCode.SHUNFENG.getMsg());
            expressInfoMap.put(LogisticsCode.EMS.getCode(), LogisticsCode.EMS.getMsg());
            return expressInfoMap;
        }
        CollUtil.newArrayList(expressContrast.getLogisticsCode()).forEach(ex ->
                expressInfoMap.put(ex, DictUtils.getDictLabel(DictDataConsts.P_EXPRESS_COMPANY, ex)));
        return expressInfoMap;

    }

    /**
     * 需要判断快递单号是否之前存在,并且物流信息不能早于抢单时间
     * 订阅快递信息,并且保存物流单号
     *
     * @param expressOrderForm
     */
    @Transactional
    public void saveExpress(ExpressOrderForm expressOrderForm) throws IOException {

        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(expressOrderForm.getOrderCode()));
        Assert.notNull(orderBO, "订单不存在");

        log.info("保存快递信息，订单号:{}，手机号码：{}，物流单号：{}", expressOrderForm.getOrderCode(), expressOrderForm.getCellphone(), expressOrderForm.getTrackingNumber());
        boolean express = Objects.equals(orderBO.getStatus(), OrderConsts.OrderStatus.DELIVERY_ING.getCode()) || Objects.equals(orderBO.getStatus(), OrderConsts.OrderStatus.ERROR.getCode());
        Assert.isTrue(express, "订单异常，请刷新页面");
        // 移除当前订单历史保存的单号
        routeSubscribeFacade.remove(new RouteSubscribeQuery().setOrderCode(orderBO.getOrderCode()));
        //需要判断快递单号是否之前存在,并且物流信息不能早于抢单时间
        DateTime offset = DateUtil.offset(DateUtil.date(), DateField.MONTH, -3);
        long count = routeSubscribeFacade.count(new RouteSubscribeQuery().setNotEqOrderCode(orderBO.getOrderCode()).setLogisticsNo(expressOrderForm.getTrackingNumber()).setGtCreateTime(offset));
        if (count > 0) {
            throw new ServiceException("快递单号3个月内已经存在,请检查快递单号信息");
        }
        // 订阅快递信息
        expressClient.subscribeExpress(new SubscribeExpressParam().setExpressNo(expressOrderForm.getTrackingNumber())
                .setExpressCode(expressOrderForm.getTrackingCompanyCode()).setOrderId(orderBO.getOrderCode()).setCellphone(expressOrderForm.getCellphone())
                .setExpressCallBackUrl(String.format(WebConstants.EXPRESS_NOTIFY_URL, ruoYiConfig.getHost())));
        // 保存订阅信息
        RouteSubscribeParam expressMidParam = new RouteSubscribeParam().setCreateTime(DateUtil.date()).setLogisticsCode(expressOrderForm.getTrackingCompanyCode())
                .setTraceStatus(ExpressCallbackStatusCode.POLLING_STATUS.getCode()).setLogisticsNo(expressOrderForm.getTrackingNumber()).setPhone(expressOrderForm.getCellphone())
                .setStatus(RouteSubscribeConsts.Status.subscribe.getValue()).setOrderCode(orderBO.getOrderCode());
        routeSubscribeFacade.save(expressMidParam);
        // 冗余数据
        tradeOrderFacade.update(new TradeOrderParam().setTrackingCompany(expressOrderForm.getTrackingCompany()).setTrackingNumber(expressOrderForm.getTrackingNumber())
                , new TradeOrderQuery().setOrderId(expressOrderForm.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        // 修改订单数据
        OrderParam orderParam = new OrderParam().setSendTime(DateUtil.date());
        // 不需要验证平台二销售的
        if (!WebConstants.PLATFORM_VER.contains(orderBO.getPlatform())) {
            orderParam.setStatus(OrderConsts.OrderStatus.DELIVERY_END.getCode());
            //  创建入库单
            wdtClient.stockInPush(builderStockIn(orderBO));
        }
        // 如果已经验证了 二销 ， 现在填写了物流单号
        if ( Objects.equals(OrderConsts.OrderSubStatus.WAIT_EXPRESS.getCode(), orderBO.getSubStatus())){
            orderParam.setStatus(OrderConsts.OrderStatus.DELIVERY_END.getCode());
            //  创建入库单
            wdtClient.stockInPush(builderStockIn(orderBO));
        }
        // 更新订单发货时间,和子状态
        orderFacade.update(orderParam, new OrderQuery().setOrderCode(orderBO.getOrderCode()));

    }


    /**
     * 创建入库单
     *
     * @param orderBO
     * @return
     */
    private StockInInfoParam builderStockIn(OrderBO orderBO) {
        List<ImeiBO> list = iMeiFacade.list(new ImeiQuery().setOrderId(orderBO.getOrderCode()));
        List<String> imeiList = null;
        if (Objects.equals("小米", orderBO.getBrand())) {
            imeiList = list.stream().map(ImeiBO::getImel).collect(Collectors.toList());
        } else {
            imeiList = list.stream().map(ImeiBO::getSn).collect(Collectors.toList());
        }

        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        String companyName = tradeOrderBO.getTradeNickName();

        StockInInfoGoodsList stockInInfoGoodsList = StockInInfoGoodsList.builder().spec_no(orderBO.getSkuCode()).num(new BigDecimal(orderBO.getQuantity()))
                .remark(String.format("代发订单号:%s,供应商名称:%s", orderBO.getOrderCode(), companyName)).stockin_price(tradeOrderBO.getTradePrice()).sn_list(imeiList).build();

        return StockInInfoParam.builder().outer_no(orderBO.getOrderCode()).warehouse_no(ruoYiConfig.getWarehouseNo())
                .is_check(1).goods_list(Collections.singletonList(stockInInfoGoodsList)).build();
    }

    /**
     * 快递100 回调接口
     *
     * @param param
     * @param sign
     * @return
     */
    public SubscribeResp kdCallback(String param, String sign) {
        log.info("快递100订阅推送回调结果|param={}|sign={}", param, sign);
        //去除换行符
        param = param.replace("\r", "\\r").replace("\n", "\\n");

        CallbackExpressResult parse = null;
        String flg = null;
        try {
            if (StrUtil.isBlank(param)) {
                // 接收信息为空
                return null;
            }
            parse = JacksonUtil.parse(param, CallbackExpressResult.class);
        } catch (Exception e) {
            log.error("快递100订阅推送回调异常", e);
            flg = e.getMessage();
        }
        if (Objects.nonNull(parse)) {
            // 保存数据
            this.saveData(parse);
            // 处理业务信息
            afterCallbackHandler(parse);
        }
        return StrUtil.isBlank(flg) ? Kd100SubscribeResp.initSucceedKd() : Kd100SubscribeResp.initFailKd(flg);

    }

    /**
     * 保存路由信息
     *
     * @param backResp
     */
    private void saveData(CallbackExpressResult backResp) {


        if (Objects.isNull(backResp)) {
            return;
        }
        SubscribePushResult lastResult = backResp.getLastResult();
        RouteSubscribeParam expressParam = new RouteSubscribeParam();
        // 查询订阅状态
        setStatus(expressParam, backResp, lastResult);
        // 解析出发地,目的地
        setRouteFromAndTo(expressParam, lastResult);
        // 保存物流详情
        expressParam.setRouteInfo(JacksonUtil.toJson(lastResult.getRouteInfo()));
        // 获取data数据集，解析后存入数据库
        setDetailInfo(lastResult, expressParam);
        expressParam.setUpdateTime(DateUtils.getNowDate());
        routeSubscribeFacade.update(expressParam, new RouteSubscribeQuery().setLogisticsNo(lastResult.getNu())
                .setLogisticsCode(lastResult.getCom()));
    }

    /**
     * 设置物流详情
     *
     * @param lastResult   lastResult
     * @param expressParam param
     */
    private void setDetailInfo(SubscribePushResult lastResult, RouteSubscribeParam expressParam) {
        if (CollUtil.isNotEmpty(lastResult.getData())) {
            expressParam.setDetailInfo(JacksonUtil.toJson(lastResult.getData()));
            // 拿取最新的轨迹时间
            expressParam.setNewRouteDate(DateUtils.parseDate(lastResult.getData().get(0).getFtime()));
        }
    }

    /**
     * 设置出发地,目的地
     *
     * @param lastResult   lastResult
     * @param expressParam param
     */
    private void setRouteFromAndTo(RouteSubscribeParam expressParam, SubscribePushResult lastResult) {
        if (!Objects.isNull(lastResult.getRouteInfo())) {
            LogisticsRouteInfo.Info from = lastResult.getRouteInfo().getFrom();
            LogisticsRouteInfo.Info to = lastResult.getRouteInfo().getTo();
            if (!Objects.isNull(from)) {
                expressParam.setRouteFrom(from.getNumber());
            }
            if (!Objects.isNull(to)) {
                expressParam.setRouteTo(to.getNumber());
            }
        }
    }


    /**
     * 设置状态
     *
     * @param backResp        回调结果
     * @param expressMidParam param
     * @param lastResult      lastResult
     */
    private void setStatus(RouteSubscribeParam expressMidParam, CallbackExpressResult backResp, SubscribePushResult lastResult) {
        expressMidParam.setTraceStatus(backResp.getStatus());
        expressMidParam.setRemark(backResp.getMessage());
        expressMidParam.setLogisticsStatus(lastResult.getState());
    }

    private void afterCallbackHandler(CallbackExpressResult parse) {

        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setLogisticsNo(parse.getLastResult().getNu()));
        if (Objects.isNull(routeSubscribeBO)) {
            log.info("订单单号，未查询到,快递单号：{}", parse.getLastResult().getNu());
            return;
        }
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(routeSubscribeBO.getOrderCode()));
        if (Objects.isNull(orderBO)) {
            log.info("订单单号，未查询到，订单单号：{}", routeSubscribeBO.getOrderCode());
            return;
        }
        List<Integer> deliveryEndCode = List.of(OrderConsts.OrderStatus.DELIVERY_END.getCode(), OrderConsts.OrderStatus.TRANSIT.getCode());
        if (!deliveryEndCode.contains(orderBO.getStatus())) {
            log.info("订单不需要处理，status={},subStatus={} 不需要处理 orderId={}, no={}", orderBO.getStatus(), orderBO.getSubStatus(),
                    orderBO.getOrderCode(), parse.getLastResult().getNu());
            return;
        }
        if (!Objects.equals(OrderConsts.OrderType.O2O.getCode(), orderBO.getOrderType())) {
            log.info("订单不是一件代发退出, orderId={}, no={}", orderBO.getOrderCode(), parse.getLastResult().getNu());
            return;
        }
        LogisticsContext context = new LogisticsContext(parse.getStatus(), parse.getLastResult(), Boolean.TRUE);
        context.doAction(orderBO.getOrderCode());
        //返还保证金
        if(Objects.isNull(orderBO.getShipmentsTime())){
            returnBond(orderBO.getOrderCode(), parse.getLastResult().getState());
        }
    }


    /**
     * 返还保证金
     *
     * @param orderId 订单id
     * @param state   物流状态
     */
    private void returnBond(String orderId, String state) {
        if (LogisticsStatus.query(state)) {
            return;
        }
        //查询抢单记录表
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderId).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        if (Objects.isNull(tradeOrderBO)) {
            return;
        }
        BigDecimal bigDecimal = companyCapitalBizService.calAmount(tradeOrderBO.getQuantity());
        //
        companyCapitalFacade.unFreeze(new CompanyCapitalLogParam().setCompanyId(tradeOrderBO.getTradeCompanyId()).setUpdateTime(tradeOrderBO.getUpdateTime()).setAddAmount(bigDecimal)
                .setOrderNo(orderId).setType(CompanyCapitalConsts.LogTypes.ORDER.getCode()).setTradeId(tradeOrderBO.getId()));
    }


    public OrderExpressVO queryOrderRoute(String orderCode) {
        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setOrderCode(orderCode));
        return OrderConvert.INSTANCE.toRouteVO(routeSubscribeBO);
    }


}
