package com.ruoyi.biz.order;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.bill.facade.IPayerConfigFacade;
import com.ruoyi.bill.facade.IPayerFacade;
import com.ruoyi.bill.model.bo.PayerBO;
import com.ruoyi.bill.model.bo.PayerConfigBO;
import com.ruoyi.bill.model.query.PayerConfigQuery;
import com.ruoyi.bill.model.query.PayerQuery;
import com.ruoyi.biz.address.SmartParse;
import com.ruoyi.biz.address.domain.AddressInfo;
import com.ruoyi.biz.bill.BillBizService;
import com.ruoyi.biz.company.CompanyCapitalBizService;
import com.ruoyi.biz.sys.IDictDistrictBizService;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.capital.model.consts.CapitalConsts;
import com.ruoyi.capital.model.consts.CompanyCapitalConsts;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.common.constant.OrderSortConsts;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.Arith;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.weebhook.QWRobotUtil;
import com.ruoyi.consts.AdminConsts;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.consts.LogisticsCode;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.mapper.order.OrderConvert;
import com.ruoyi.mapper.rule.RuleConvert;
import com.ruoyi.order.domain.dto.OrderStatusDTO;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.*;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.ImeiConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.*;
import com.ruoyi.order.model.param.RuleParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.ImeiQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.product.facade.IProductSkuFacade;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.query.ProductSkuQuery;
import com.ruoyi.rule.model.bo.RuleBO;
import com.ruoyi.rule.model.consts.RuleConsts;
import com.ruoyi.system.model.bo.DictDistrictBO;
import com.ruoyi.user.domain.User;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.facade.IUserFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.bo.UserBO;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.user.model.query.UserQuery;
import com.ruoyi.web.form.order.*;
import com.ruoyi.web.form.rule.RuleForm;
import com.ruoyi.web.vo.order.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.ruoyi.consts.DictEnum.WEB_HOOK_FOLLOW_ORDER;

@Slf4j
@Service
public class OrderBizService {

    @Autowired
    SmartParse smartParse;

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
    ICompanyCapitalFacade companyCapitalFacade;

    @Autowired
    CompanyCapitalBizService companyCapitalBizService;

    @Autowired
    IProductSkuFacade productSkuFacade;

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @Autowired
    IImeiFacade imeiFacade;

    @Autowired
    ICompanyFacade companyFacade;

    @Autowired
    BillBizService billBizService;

    @Autowired
    IPayerFacade payerFacade;

    @Autowired
    IUserFacade userFacade;

    @Autowired
    IPayerConfigFacade payerConfigFacade;


    public String sortField(Integer status) {

        OrderConsts.OrderStatus byCode = OrderConsts.OrderStatus.getByCode(status);
        String sortType = null;
        if (Objects.isNull(byCode)) {
            return OrderSortConsts.ALL_ORDER;
        }
        // 根据状态设置排序规则 & 补充状态列表（如需要）
        switch (byCode) {
            case NEW:
                sortType = OrderSortConsts.NEW_ORDER;
                break;
            case WAIT:
                sortType = OrderSortConsts.WAIT_ORDER;
                break;
            case TRADING:
                sortType = OrderSortConsts.TRADING_ORDER;
                break;
            case DELIVERY_ING:
                sortType = OrderSortConsts.DELIVERY_ING_ORDER;
                break;
            case DELIVERY_END:
                sortType = OrderSortConsts.DELIVERY_TODAY_ORDER;
                break;
            case CHASE_ORDER:
                sortType = OrderSortConsts.REFUND_NOTARIZE_TIME;
                break;
            case TRANSIT:
                sortType = OrderSortConsts.TRANSIT_ORDER;
                break;
            case REVOKE:
                sortType = OrderSortConsts.ERROR_ORDER;
                break;
            case ERROR:
                sortType = OrderSortConsts.ERROR_ORDER;
                break;
            case ENDING:
            case RECEIPT:
                // 合并“完成”类状态：支持传 ENDING 或 RECEIPT，但统一查两个
                sortType = OrderSortConsts.ENDING_AND_RECEIPT;
                break;
            case AFTER_SALES:
                sortType = OrderSortConsts.ENDING_AND_RECEIPT; // 假设售后也用这个排序，按需调整
                break;
            default:
                sortType = OrderSortConsts.ALL_ORDER;
                break;
        }
        return sortType;

    }

    /**
     * 待发布、报价中、待发货、追单
     * 订单状态下班中的订单列表
     *
     * @param orderNewParam
     * @return
     */
    public PageBO<OrderListVO> orderList(final OrderNewForm orderNewParam, final PageParamV2 pageParamV2) {
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);
        OrderQuery orderQuery = OrderConvert.INSTANCE.paramToQuery(orderNewParam).setCreateTimeStart(dateTime);
        PageBO<OrderBO> pageBO = orderFacade.listPage(orderQuery, pageParamV2);

        List<OrderListVO> orderNewVOList = OrderConvert.INSTANCE.toWaitVOList(pageBO.getData());
        // 完善省市
        Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
        for (OrderListVO orderListVO : orderNewVOList) {
            orderListVO.setProvinceName(provinceMap.get(orderListVO.getProvince()));
            Map<Long, String> cityMap = dictDistrictBizService.listCity(orderListVO.getProvince()).stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
            orderListVO.setCityName(cityMap.get(orderListVO.getCity()));
        }
        if (List.of(OrderConsts.OrderStatus.NEW.getCode(), OrderConsts.OrderStatus.REVOKE.getCode()).contains(orderNewParam.getStatus())) {
            return new PageBO<OrderListVO>().setData(orderNewVOList).setTotal(pageBO.getTotal());
        }

//        // 完善挂单
//        for (OrderListVO orderListVO : orderNewVOList) {
//            HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderListVO.getOrderCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
//            if (Objects.isNull(hangingOrderBO)) {
//                continue;
//            }
//            // 设置每次报价时间, 履约时效、价格等等
//            orderListVO.setQuotationInterval(hangingOrderBO.getQuotationInterval()).setDeliveryTime(hangingOrderBO.getDeliveryTime());
//            orderListVO.setAccountingPeriod(hangingOrderBO.getAccountingPeriod()).setOtherRequire(hangingOrderBO.getOtherRequire());
//            orderListVO.setPriceHighest(hangingOrderBO.getPriceHighest()).setPriceHign(hangingOrderBO.getPriceHign()).setPriceLow(hangingOrderBO.getPriceLow()).setPriceLowest(hangingOrderBO.getPriceLowest());
//            orderListVO.setPriceHighestStatus(hangingOrderBO.getPriceHighestStatus()).setPriceHignStatus(hangingOrderBO.getPriceHignStatus()).setPriceLowStatus(hangingOrderBO.getPriceLowStatus()).setPriceLowestStatus(hangingOrderBO.getPriceLowestStatus());
//            // 判断是否需要倒计时
//            List<Integer> codeList = List.of(TradeOrderConsts.TradeStatus.EXPIRED.getCode(), TradeOrderConsts.TradeStatus.SUCCESS.getCode());
//            if (codeList.contains(hangingOrderBO.getPriceHighestStatus()) || codeList.contains(hangingOrderBO.getPriceHignStatus()) || codeList.contains(hangingOrderBO.getPriceLowStatus()) || codeList.contains(hangingOrderBO.getPriceLowestStatus())) {
//                List<Integer> statusList = List.of(TradeOrderConsts.TradeStatus.SUCCESS.getCode(), TradeOrderConsts.TradeStatus.EXPIRED.getCode());
//                TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setHangOrderId(hangingOrderBO.getId()).setOrderId(orderListVO.getOrderCode()).setStatusList(statusList));
//                if (Objects.isNull(tradeOrderBO)) {
//                    continue;
//                }
//                orderListVO.setLastCompeteTime(tradeOrderBO.getCreateTime()).setTradePrice(tradeOrderBO.getTradePrice()).setTradeNickName(tradeOrderBO.getTradeNickName())
//                        .setTradeUserName(tradeOrderBO.getTradeUserName()).setTradeUserPhone(tradeOrderBO.getTradeUserPhone())
//                        .setTrackingNumber(tradeOrderBO.getTrackingNumber()).setTrackingCompany(tradeOrderBO.getTrackingCompany()).setTradeCompanyName(tradeOrderBO.getTradeCompanyName());
//            }
//        }
        return new PageBO<OrderListVO>().setData(orderNewVOList).setTotal(pageBO.getTotal());
    }

    /**
     * 已经发货的订单 ， 在途、待发货、已经签收、异常订单
     *
     * @param orderNewParam
     * @param pageParamV2
     * @return
     */
    public PageBO<OrderListVO> orderSendList(OrderNewForm orderNewParam, PageParamV2 pageParamV2) {
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);

        OrderQuery orderQuery = OrderConvert.INSTANCE.paramToQuery(orderNewParam).setCreateTimeStart(dateTime);
        orderQuery.setSort(this.sortField(orderNewParam.getStatus()));
        PageBO<SendOrderLisBO> sendOrderLisBOPageBO = orderFacade.sendListPage(orderQuery, pageParamV2);

        List<OrderListVO> orderNewVOList = OrderConvert.INSTANCE.toOrderVOList(sendOrderLisBOPageBO.getData());
        // 完善省市
        Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
        for (OrderListVO orderListVO : orderNewVOList) {
            orderListVO.setProvinceName(provinceMap.get(orderListVO.getProvince()));
            Map<Long, String> cityMap = dictDistrictBizService.listCity(orderListVO.getProvince()).stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
            orderListVO.setCityName(cityMap.get(orderListVO.getCity()));
        }

        return new PageBO<OrderListVO>().setData(orderNewVOList).setTotal(sendOrderLisBOPageBO.getTotal());
    }


    /**
     * 待发布、报价中、待发货、追单
     * 订单状态下班中的订单列表
     *
     * @param orderNewParam
     * @return
     */
    public List<OrderListVO> orderListExport(final OrderNewForm orderNewParam) {
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);
        OrderQuery orderQuery = OrderConvert.INSTANCE.paramToQuery(orderNewParam).setCreateTimeStart(dateTime);
        List<OrderBO> pageBO = orderFacade.list(orderQuery);

        List<OrderListVO> orderNewVOList = OrderConvert.INSTANCE.toWaitVOList(pageBO);
        // 完善省市
        Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
        for (OrderListVO orderListVO : orderNewVOList) {
            orderListVO.setProvinceName(provinceMap.get(orderListVO.getProvince()));
            Map<Long, String> cityMap = dictDistrictBizService.listCity(orderListVO.getProvince()).stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
            orderListVO.setCityName(cityMap.get(orderListVO.getCity()));
        }
        if (List.of(OrderConsts.OrderStatus.NEW.getCode(), OrderConsts.OrderStatus.REVOKE.getCode()).contains(orderNewParam.getStatus())) {
            return orderNewVOList;
        }

        // 完善挂单
        for (OrderListVO orderListVO : orderNewVOList) {
            HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderListVO.getOrderCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
            if (Objects.isNull(hangingOrderBO)) {
                continue;
            }
            // 设置每次报价时间, 履约时效、价格等等
            orderListVO.setQuotationInterval(hangingOrderBO.getQuotationInterval()).setDeliveryTime(hangingOrderBO.getDeliveryTime());
            orderListVO.setAccountingPeriod(hangingOrderBO.getAccountingPeriod());
            orderListVO.setPriceHighest(hangingOrderBO.getPriceHighest()).setPriceHign(hangingOrderBO.getPriceHign()).setPriceLow(hangingOrderBO.getPriceLow()).setPriceLowest(hangingOrderBO.getPriceLowest());
            orderListVO.setPriceHighestStatus(hangingOrderBO.getPriceHighestStatus()).setPriceHignStatus(hangingOrderBO.getPriceHignStatus()).setPriceLowStatus(hangingOrderBO.getPriceLowStatus()).setPriceLowestStatus(hangingOrderBO.getPriceLowestStatus());
            // 判断是否需要倒计时
            List<Integer> codeList = List.of(TradeOrderConsts.TradeStatus.EXPIRED.getCode(), TradeOrderConsts.TradeStatus.SUCCESS.getCode());
            if (codeList.contains(hangingOrderBO.getPriceHighestStatus()) || codeList.contains(hangingOrderBO.getPriceHignStatus()) || codeList.contains(hangingOrderBO.getPriceLowStatus()) || codeList.contains(hangingOrderBO.getPriceLowestStatus())) {
                List<Integer> statusList = List.of(TradeOrderConsts.TradeStatus.SUCCESS.getCode(), TradeOrderConsts.TradeStatus.EXPIRED.getCode());
                TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setHangOrderId(hangingOrderBO.getId()).setOrderId(orderListVO.getOrderCode()).setStatusList(statusList));
                if (Objects.isNull(tradeOrderBO)) {
                    continue;
                }
                orderListVO.setLastCompeteTime(tradeOrderBO.getCreateTime()).setTradePrice(tradeOrderBO.getTradePrice()).setTradeNickName(tradeOrderBO.getTradeNickName())
                        .setTradeUserName(tradeOrderBO.getTradeUserName()).setTradeUserPhone(tradeOrderBO.getTradeUserPhone())
                        .setTrackingNumber(tradeOrderBO.getTrackingNumber()).setTrackingCompany(tradeOrderBO.getTrackingCompany());
            }
        }
        return orderNewVOList;
    }


    /**
     * 撤销订单
     *
     * @param revokeParam
     */
    @Transactional
    public void revokeList(final RevokeParam revokeParam) {

        for (String orderCode : revokeParam.getOrderCodeList()) {
            revoke(orderCode, revokeParam.getRevokeCode());
        }

    }

    /**
     * 撤销订单
     *
     */
    @Transactional
    public void revoke(final String orderCode, final Integer revokeCode) {
        log.info("撤销/追单订单：{}, 撤销原因:{}", orderCode, revokeCode);
        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderCode).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        if (Objects.isNull(hangingOrderBO)) {
            // 还没挂单，直接撤销，
            orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.REVOKE.getCode()).setSubStatus(OrderConsts.OrderSubStatus.REVOKE_NEW.getCode())
                    .setRevokeType(revokeCode).setUpdateTime(DateUtil.date()), new OrderQuery().setOrderCode(orderCode));
            return;
        }

        // 追单1 ， 还未成交，还没人抢单
        List<TradeOrderBO> tradeOrderBOList = tradeOrderFacade.list(new TradeOrderQuery().setOrderId(orderCode).setHangOrderId(hangingOrderBO.getId()));
        if (CollectionUtil.isEmpty(tradeOrderBOList)) {
            // 设置挂单记录失效
            hangingOrderFacade.update(new HangingOrderParam().setStatus(HandingOrderConsts.Status.FAILURE.getCode()), new HangingOrderQuery().setId(hangingOrderBO.getId()));
            // 设置订单失效
            orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.REVOKE.getCode()).setSubStatus(OrderConsts.OrderSubStatus.REVOKE_TRADING.getCode())
                    .setUpdateTime(DateUtil.date()).setRevokeType(revokeCode), new OrderQuery().setOrderCode(orderCode));
            return;
        }
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        // 追单2 ， 已经抢单，还没成交
        Set<Integer> tradeStatusSet = tradeOrderBOList.stream().map(TradeOrderBO::getStatus).collect(Collectors.toSet());
        if (!tradeStatusSet.contains(TradeOrderConsts.TradeStatus.SUCCESS.getCode())) {
            // 设置抢单记录失效
            tradeOrderFacade.update(new TradeOrderParam().setStatus(TradeOrderConsts.TradeStatus.EXPIRED.getCode()), new TradeOrderQuery().setOrderId(hangingOrderBO.getOrderId()).setHangOrderId(hangingOrderBO.getId()));
            // 设置挂单记录失效
            hangingOrderFacade.update(new HangingOrderParam().setStatus(HandingOrderConsts.Status.FAILURE.getCode()), new HangingOrderQuery().setId(hangingOrderBO.getId()));
            // 设置订单失效
            orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.REVOKE.getCode()).setSubStatus(OrderConsts.OrderSubStatus.REVOKE_TRADING.getCode())
                    .setUpdateTime(DateUtil.date()).setRevokeType(revokeCode), new OrderQuery().setOrderCode(orderCode));
            //  退还保证金
            for (TradeOrderBO tradeOrderBO : tradeOrderBOList) {
                tradeOrderFacade.update(new TradeOrderParam().setStatus(TradeOrderConsts.TradeStatus.TAKEN.getCode()), new TradeOrderQuery().setId(tradeOrderBO.getId()));
                //  退保证金
                companyCapitalFacade.unFreeze(new CompanyCapitalLogParam().setCompanyId(tradeOrderBO.getTradeCompanyId()).setUpdateTime(tradeOrderBO.getUpdateTime()).setAddAmount(companyCapitalBizService.calAmount(orderBO.getQuantity()))
                        .setOrderNo(hangingOrderBO.getOrderId()).setType(CompanyCapitalConsts.LogTypes.ORDER.getCode()).setTradeId(tradeOrderBO.getId()));
            }

            return;
        }
        // 追单3 ， 已经成交
        List<Integer> ordreCodeList = List.of(OrderConsts.OrderStatus.DELIVERY_ING.getCode(), OrderConsts.OrderStatus.ERROR.getCode(), OrderConsts.OrderStatus.DELIVERY_END.getCode(), OrderConsts.OrderStatus.TRANSIT.getCode());
        Optional<TradeOrderBO> tradeOrderBOOptional = tradeOrderBOList.stream().filter(e -> Objects.equals(TradeOrderConsts.TradeStatus.SUCCESS.getCode(), e.getStatus())).findFirst();
        if (ordreCodeList.contains(orderBO.getStatus())) {
            //
            OrderParam orderParam = new OrderParam().setStatus(OrderConsts.OrderStatus.CHASE_ORDER.getCode())
                    .setSubStatus(OrderConsts.OrderSubStatus.DELIVERY_ING_BACK.getCode())
                    .setUpdateTime(DateUtil.date()).setRevokeType(revokeCode);
            if (Objects.nonNull(orderBO.getSendTime())) {
                orderParam.setSubStatus(OrderConsts.OrderSubStatus.DELIVERY_END_BACK.getCode());
            }
            orderFacade.update(orderParam, new OrderQuery().setOrderCode(orderCode));
            // 退还保证金
            companyCapitalFacade.unFreeze(new CompanyCapitalLogParam().setCompanyId(hangingOrderBO.getLastCompeteCompany()).setUpdateTime(DateUtil.date()).setAddAmount(companyCapitalBizService.calAmount(orderBO.getQuantity()))
                    .setOrderNo(orderBO.getOrderCode()).setType(CompanyCapitalConsts.LogTypes.ORDER.getCode()).setTradeId(tradeOrderBOOptional.get().getId()));
            // 提供追单资金
            BigDecimal amount = Objects.nonNull(orderBO.getShipmentsTime()) ? AdminConsts.REVOKE_AMOUNT_SHIPMENTS : AdminConsts.REVOKE_AMOUNT;
            CompanyCapitalLogParam capitalDetailParam = new CompanyCapitalLogParam().setOutAmount(BigDecimal.ZERO)
                    .setOrderNo(orderBO.getOrderCode()).setCompanyId(hangingOrderBO.getLastCompeteCompany()).setAddAmount(amount)
                    .setType(CompanyCapitalConsts.LogTypes.CHASE.getCode());
            companyCapitalFacade.changeAvailable(capitalDetailParam);
            // imei 撤销
            imeiFacade.update(new ImeiParam().setActivated(ImeiConsts.Activated.CANCEL.getCode()), new ImeiQuery().setOrderId(orderCode));
            // 发送追单消息
            RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setOrderCode(orderCode));
            sendMessage(revokeCode, Objects.nonNull(routeSubscribeBO) ? routeSubscribeBO.getLogisticsNo() : "", orderBO, tradeOrderBOOptional.get());
            return;
        }
    }

    /**
     * 发送消息到企业微信
     *
     * @param logisticsNo 物流单号
     * @param orderBo     订单信息
     */
    private void sendMessage(Integer revokeCode, String logisticsNo, OrderBO orderBo, TradeOrderBO tradeOrderBO) {
        OrderConsts.RevokeType revokeType = OrderConsts.RevokeType.getByCode(revokeCode);
        String uncollectedKey = DictUtils.getDictValue(WEB_HOOK_FOLLOW_ORDER.getValue(), WEB_HOOK_FOLLOW_ORDER.getLabel());
        if (StrUtil.isNotBlank(uncollectedKey) && Objects.nonNull(revokeType)
                && Objects.nonNull(orderBo) && Objects.nonNull(tradeOrderBO)) {
            String sb = "**订单已自动退货追单 [" + "<font color='warning'>  " + revokeType.getDesc() + "</font>]**\r\n" +
                    ">内部单号：<font color='info'>" + orderBo.getOrderCode() + "</font>\r\n" +
                    ">物流单号：<font color='info'>" + logisticsNo + "</font>\r\n" +
                    ">地址信息：<font color='info'>" + orderBo.getAddressee() + " " + orderBo.getPhone() + " " + orderBo.getReceivingAddress() + "</font>\r\n" +
                    ">旺店通单号：<font color='info'>" + orderBo.getErpOrderId() + "</font>\r\n" +
                    ">原始单号：<font color='info'>" + orderBo.getOriginalOrderId() + "</font>\r\n" +
                    ">供应商：<font color='info'>" + tradeOrderBO.getTradeNickName() + "</font>\r\n";
            QWRobotUtil.sendMarkdownMsg(uncollectedKey, sb);
        }
    }

    /**
     * 订单报价
     *
     * @param ruleForm
     */
    @Transactional
    public void quotation(final RuleForm ruleForm, Long userId) {


        OrderBO orderBo = orderFacade.getOne(new OrderQuery().setOrderCode(ruleForm.getOrderCode()));
        if (Objects.isNull(orderBo)) {
            throw new ServiceException("订单异常");
        }

        RuleParam ruleParam = RuleConvert.INSTANCE.toParam(ruleForm).setSkuCode(orderBo.getSkuCode()).setSkuName(orderBo.getSkuName()).setCreateBy(userId)
                .setBrand(orderBo.getBrand()).setProductName(orderBo.getProductName()).setCategory(orderBo.getCategory()).setStatus(RuleConsts.Status.NORMAL.getCode());
        if (ruleParam.getRuleRange() == RuleConsts.Range.PROVINCE.getCode()) {
            ruleParam.setProvince(orderBo.getProvince());
        }
        //保存规则
        RuleBO ruleBO = ruleBizService.saveOrUpdate(ruleParam);
        // 更新报价
        this.changeHanging(orderBo.getOrderCode());
        //执行规则
        ruleBizService.execute(ruleBO);
    }

    /**
     * 修改订单报价状态
     *
     * @param orderCode
     */
    @Transactional
    private void changeHanging(String orderCode) {
        if (StrUtil.isEmpty(orderCode)) {
            return;
        }
        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderCode).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        if (Objects.isNull(hangingOrderBO)) {
            return;
        }
        // 判断是否被其他人接单
        long count = tradeOrderFacade.count(new TradeOrderQuery().setOrderId(orderCode).setHangOrderId(hangingOrderBO.getId()));
        if (count > 0) {
            return;
        }
        // 修改挂单
        hangingOrderFacade.update(new HangingOrderParam().setStatus(HandingOrderConsts.Status.FAILURE.getCode()),
                new HangingOrderQuery().setOrderId(orderCode).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        // 修改订单状态
        orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.WAIT.getCode()),
                new OrderQuery().setOrderCode(orderCode));
    }


    /**
     * 订单状态下班的省的订单数量
     */
    public List<OrderAreaCountVO> provinceCount(final ProvinceForm provinceForm) {
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);

        List<ProvinceCountBO> provinceCountBOS = orderFacade.provinceCount(new OrderQuery().setCreateTimeStart(dateTime)
                .setStatus(provinceForm.getStatus()).setBrand(provinceForm.getBrand()).setOrderType(OrderConsts.OrderType.O2O.getCode()));
        if (CollectionUtil.isEmpty(provinceCountBOS)) {
            return Collections.EMPTY_LIST;
        }
        List<OrderAreaCountVO> list = OrderConvert.INSTANCE.toOrderAreaCountVO(provinceCountBOS);
        for (OrderAreaCountVO provinceCount : list) {
            Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
            provinceCount.setProvinceName(provinceMap.get(provinceCount.getProvince()));
        }
        return list;


    }


    /**
     * 订单状态下班的省的订单数量
     */
    public List<BrandCountVO> brandCount(final BrandForm brandForm) {
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);

        List<BrandCountBO> brandCountBOS = orderFacade.brandCount(new OrderQuery().setStatus(brandForm.getStatus())
                .setCreateTimeStart(dateTime).setProvince(brandForm.getProvince()).setOrderType(OrderConsts.OrderType.O2O.getCode()));

        return OrderConvert.INSTANCE.toBrandCountVO(brandCountBOS);
    }

    /**
     * 订单转异常
     *
     * @param errorOrder
     */
    public void error(final ErrorOrder errorOrder) {

        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(errorOrder.getOrderCode()));
        if (Objects.isNull(orderBO)) {
            throw new ServiceException("订单不存在");
        }
        List<Integer> codeList = List.of(OrderConsts.OrderStatus.DELIVERY_END.getCode(), OrderConsts.OrderStatus.TRANSIT.getCode());
        if (!codeList.contains(orderBO.getStatus())) {
            throw new ServiceException("订单状态异常");
        }
        orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.ERROR.getCode()).setSubStatus(errorOrder.getErrorCode()), new OrderQuery().setOrderCode(errorOrder.getOrderCode()));

    }

    /**
     * 添加订单号
     *
     * @param orderAddForm
     */
    public void add(final OrderAddForm orderAddForm) {
        caseOrderStyle(orderAddForm);
        if (StrUtil.isBlank(orderAddForm.getAddressee()) && StrUtil.isBlank(orderAddForm.getPhone())) {
            ParseUserInfoUtil.ParsedAddress parse = ParseUserInfoUtil.parse(orderAddForm.getAddress());
            orderAddForm.setAddressee(parse.getName());
            orderAddForm.setPhone(parse.getPhone());
            orderAddForm.setAddress(parse.getAddress());
        }
        orderAddForm.setAddress(orderAddForm.getAddress().replaceAll("\\s+", ""));
        AddressInfo addressInfo = smartParse.parseAddressInfo(orderAddForm.getAddress());
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOriginalOrderId(orderAddForm.getOriginalOrderId()).setErpOrderId(orderAddForm.getErpOrderId()));
        if (Objects.isNull(orderBO)) {
            ProductSkuBO productSkuBO = productSkuFacade.getOne(new ProductSkuQuery().setSkuCode(orderAddForm.getSkuCode()));
            if (Objects.isNull(productSkuBO)) {
                log.error("订单商品编码不存在：{}", orderAddForm.getSkuCode());
                return;
            }
            OrderParam param = OrderConvert.INSTANCE.toParam(orderAddForm).setOrderType(OrderConsts.OrderType.O2O.getCode()).setOrderCode(IdUtil.objectId());
            param.setBrand(productSkuBO.getBrand()).setCategory(productSkuBO.getCategory()).setProductName(productSkuBO.getProductName()).setSkuName(productSkuBO.getSpecName());
            param.setStatus(OrderConsts.OrderStatus.NEW.getCode()).setSubStatus(OrderConsts.OrderSubStatus.NEW.getCode());
            if (Objects.nonNull(addressInfo)) {
                if (Objects.nonNull(addressInfo.getProvinceCode())) {
                    param.setProvince(Long.valueOf(addressInfo.getProvinceCode()));
                }
                if (Objects.nonNull(addressInfo.getCityCode())) {
                    param.setCity(Long.valueOf(addressInfo.getCityCode()));
                }
            }
            param.setAddressee(orderAddForm.getAddressee().replaceAll("\\s+", "")).setPhone(orderAddForm.getPhone()).setReceivingAddress(orderAddForm.getAddress().replaceAll("\\s+", ""));
            if (StrUtil.isNotBlank(param.getAddressee()) && StrUtil.isNotBlank(param.getPhone()) && StrUtil.isNotBlank(param.getReceivingAddress())) {
                param.setAddressStatus(OrderConsts.AddressStatus.SUCCESS.getCode());
            } else {
                param.setAddressStatus(OrderConsts.AddressStatus.NOT_SUPPLEMENTED.getCode());
            }
            PayerConfigBO payerConfigBO = payerConfigFacade.getOne(new PayerConfigQuery().setKeyWord(param.getShopName()));
            if (Objects.isNull(payerConfigBO)) {
                log.error("订单店铺不存在：{}", param.getShopName());
                return;
            }
            PayerBO payerBO = payerFacade.getOne(new PayerQuery().setId(payerConfigBO.getPayerId()));
            if (Objects.isNull(payerBO)) {
                log.info("订单付款人不存在：{}", payerConfigBO.getPayerId());
                return;
            }
            param.setPayerName(payerBO.getNickName());
            orderFacade.save(param);
            return;
        } else if (Objects.equals(orderBO.getStatus(), OrderConsts.OrderStatus.REVOKE.getCode())) {
            OrderParam orderParam = new OrderParam().setStatus(OrderConsts.OrderStatus.NEW.getCode()).setCreateTime(DateUtil.date()).setSubStatus(OrderConsts.OrderSubStatus.NEW_REVOKE.getCode()).setHandleApply(OrderConsts.HandleApply.NO.getCode());
            orderParam.setAddressee(orderAddForm.getAddressee()).setPhone(orderAddForm.getPhone()).setReceivingAddress(orderAddForm.getAddress());
            if (Objects.nonNull(addressInfo)) {
                if (Objects.nonNull(addressInfo.getProvinceCode())) {
                    orderParam.setProvince(Long.valueOf(addressInfo.getProvinceCode()));
                }
                if (Objects.nonNull(addressInfo.getCityCode())) {
                    orderParam.setCity(Long.valueOf(addressInfo.getCityCode()));
                }
            }
            orderFacade.update(orderParam, new OrderQuery().setOrderCode(orderBO.getOrderCode()));
        } else {
            log.info("订单已经存在:{}", orderAddForm.getOriginalOrderId());
        }

    }

    private void caseOrderStyle(OrderAddForm orderAddForm) {

        OrderConsts.OrderStyle orderStyle = OrderConsts.OrderStyle.getByName(orderAddForm.getOrderStyle());
        orderAddForm.setOrderStyle(Objects.nonNull(orderStyle) ? orderStyle.getCode().toString() : OrderConsts.OrderStyle.BILLION.getCode().toString());
    }

    /**
     * 追单订单转正常
     *
     * @param orderCode
     */
    @Transactional(rollbackFor = Exception.class)
    public void chase2normal(final String orderCode, final Long userId) {
        log.info("用户：{}，追单订单转正常:{}", userId, orderCode);
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        if (Objects.isNull(orderBO)) {
            throw new ServiceException("订单不存在");
        }
        if (!Objects.equals(orderBO.getStatus(), OrderConsts.OrderStatus.CHASE_ORDER.getCode())) {
            throw new ServiceException("订单状态异常");
        }
        DateTime date = DateUtil.date();
        tradeOrderFacade.update(new TradeOrderParam().setStatus(TradeOrderConsts.TradeStatus.TAKEN.getCode()).setUpdateTime(date),
                new TradeOrderQuery().setOrderId(orderCode).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));

        hangingOrderFacade.update(new HangingOrderParam().setStatus(HandingOrderConsts.Status.FAILURE.getCode()).setUpdateTime(date),
                new HangingOrderQuery().setOrderId(orderCode).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));

        orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.WAIT.getCode()).setSubStatus(OrderConsts.OrderSubStatus.CHAS_TIMEOUT.getCode())
                .setUpdateTime(date), new OrderQuery().setOrderCode(orderCode));
    }


    /**
     * 确定订单完结接口
     *
     * @param orderCode
     * @param userId
     */
    public void ending(String orderCode, Long userId) {
        boolean update = orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.ENDING.getCode())
                .setUpdateTime(DateUtil.date()), new OrderQuery().setOrderCode(orderCode));
        OrderBO orderBo = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        if (update) {
            billBizService.generateBill(orderBo);
        }
    }

    /**
     * 订单转异常转正常
     *
     * @param orderCode
     * @param userId
     * @param refund    1 退保证金
     */
    public void error2Ending(final String orderCode, final Long userId, Integer refund) {
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        if (Objects.isNull(orderBO)) {
            throw new ServiceException("订单不存在");
        }
        if (!Objects.equals(orderBO.getStatus(), OrderConsts.OrderStatus.ERROR.getCode())) {
            throw new ServiceException("订单状态异常");
        }
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        if (Objects.nonNull(tradeOrderBO) && refund == 1) {
            //
            companyCapitalFacade.unFreeze(new CompanyCapitalLogParam().setCompanyId(tradeOrderBO.getTradeCompanyId()).setOrderNo(orderCode)
                    .setAddAmount(CapitalConsts.CHASE).setType(CompanyCapitalConsts.LogTypes.CHASE.getCode()));
        }
        ending(orderCode, userId);
    }


    /**
     * 撤销订单报价
     *
     * @param revokeParam
     */
    public void revokeHanging(final RevokeHangingForm revokeParam) {
        if (CollectionUtil.isEmpty(revokeParam.getOrderCode())) {
            return;
        }
        for (String orderCode : revokeParam.getOrderCode()) {
            HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderCode).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
            if (Objects.isNull(hangingOrderBO)) {
                continue;
            }
            if (!Objects.equals(hangingOrderBO.getPriceHighestStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode()) ||
                    !Objects.equals(hangingOrderBO.getPriceHignStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode()) ||
                    !Objects.equals(hangingOrderBO.getPriceLowStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode()) ||
                    !Objects.equals(hangingOrderBO.getPriceLowestStatus(), TradeOrderConsts.TradeStatus.CONFIRMED.getCode())) {
                continue;
            }
            boolean update = hangingOrderFacade.update(new HangingOrderParam().setStatus(HandingOrderConsts.Status.FAILURE.getCode()),
                    new HangingOrderQuery().setId(hangingOrderBO.getId()).setPriceLowestStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode()).setPriceHighestStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode())
                            .setPriceHignStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode()).setPriceLowStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode()));
            if (!update) {
                continue;
            }
            // 修改订单状态
            orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.WAIT.getCode()).setSubStatus(OrderConsts.OrderSubStatus.WAIT_TRADING.getCode())
                    .setUpdateTime(DateUtil.date()), new OrderQuery().setOrderCode(orderCode));
        }

    }

    /**
     * 异常订单，撤销订单
     *
     * @param orderCode 订单号
     * @param userId    用户id
     */
    @Transactional
    public void error2Wait(final String orderCode, final Long userId) {
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        if (Objects.isNull(orderBO)) {
            throw new ServiceException("订单不存在");
        }
        if (!Objects.equals(orderBO.getStatus(), OrderConsts.OrderStatus.ERROR.getCode())) {
            throw new ServiceException("订单状态异常");
        }
        DateTime date = DateUtil.date();
        // 设置抢单为失效状态
        tradeOrderFacade.update(new TradeOrderParam().setStatus(TradeOrderConsts.TradeStatus.EXPIRED.getCode()).setUpdateBy(userId).setUpdateTime(date)
                , new TradeOrderQuery().setOrderId(orderCode).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        // 设置挂单为失效状态
        hangingOrderFacade.update(new HangingOrderParam().setStatus(HandingOrderConsts.Status.FAILURE.getCode()).setUpdateBy(userId).setUpdateTime(date),
                new HangingOrderQuery().setOrderId(orderCode).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        // 设置订单状态为待处理
        orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.WAIT.getCode()).setSubStatus(OrderConsts.OrderSubStatus.WAIT_ERROR.getCode()).setUpdateTime(date),
                new OrderQuery().setOrderCode(orderCode));

    }

    public List<OrderStatusVO> countHeader(DateTime dateTime) {

        List<OrderStatusDTO> orderStatusDTOS = orderFacade.countHeader(dateTime);
        return OrderConvert.INSTANCE.toOrderStatus(orderStatusDTOS);
    }

    /**
     * 当日发货导出
     *
     * @param orderNewParam
     * @return
     */
    public List<OrderDeliveryVO> orderList(OrderNewForm orderNewParam) {

        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);
        OrderQuery orderQuery = OrderConvert.INSTANCE.paramToQuery(orderNewParam).setCreateTimeStart(dateTime);
        List<OrderBO> list = orderFacade.list(orderQuery);
        List<OrderDeliveryVO> orderNewVOList = OrderConvert.INSTANCE.toOrderDeliveryVO(list);
        ArrayList<OrderDeliveryVO> objects = new ArrayList<>();


        for (OrderDeliveryVO orderDeliveryVO : orderNewVOList) {
            List<ImeiBO> imeiBOS = imeiFacade.list(new ImeiQuery().setOrderId(orderDeliveryVO.getOrderCode()).setActivated(ImeiConsts.Activated.SUCCESS.getCode()).setPlatformImei(ImeiConsts.PlatformImei.NORMAL.getCode()));
            if (CollectionUtil.isEmpty(imeiBOS)) {
                continue;
            }
            OrderDeliveryVO orderDeliveryB = new OrderDeliveryVO();
            RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setOrderCode(orderDeliveryVO.getOrderCode()));
            orderDeliveryB.setPlatform(orderDeliveryVO.getPlatform());
            orderDeliveryB.setShopName(orderDeliveryVO.getShopName());
            orderDeliveryB.setProductName(orderDeliveryVO.getProductName());
            orderDeliveryB.setSkuName(orderDeliveryVO.getSkuName());
            for (ImeiBO imeiBO : imeiBOS) {
                orderDeliveryB.setImei(imeiBO.getImel());
                orderDeliveryB.setSn(imeiBO.getSn());
                orderDeliveryB.setErpOrderId(orderDeliveryVO.getErpOrderId());
                orderDeliveryB.setOriginalOrderId(orderDeliveryVO.getOriginalOrderId());
                orderDeliveryB.setSkuCode(orderDeliveryVO.getSkuCode());
            }
            if (Objects.nonNull(routeSubscribeBO)) {
                orderDeliveryB.setTrackingNumber(routeSubscribeBO.getLogisticsNo());
                LogisticsCode logisticsCode = LogisticsCode.getByCode(routeSubscribeBO.getLogisticsCode());
                orderDeliveryB.setTrackingCompany(logisticsCode.getMsg());
            }
            TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderDeliveryVO.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
            if (Objects.nonNull(tradeOrderBO)) {
                orderDeliveryB.setTradeCompanyName(tradeOrderBO.getTradeNickName());
                orderDeliveryB.setTradePrice(tradeOrderBO.getTradePrice());
            }
            objects.add(orderDeliveryB);
        }
        return objects;
    }

    /**
     * 待发布、报价中、待发货、追单
     * 订单状态下班中的订单列表
     *
     * @param orderNewParam
     * @return
     */
    public List<OrderListVO> tradingExport(final OrderNewForm orderNewParam) {
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);
        OrderQuery orderQuery = OrderConvert.INSTANCE.paramToQuery(orderNewParam).setCreateTimeStart(dateTime);
        List<OrderBO> list = orderFacade.list(orderQuery, null);

        List<OrderListVO> orderNewVOList = OrderConvert.INSTANCE.toWaitVOList(list);
        // 完善省市
        Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
        for (OrderListVO orderListVO : orderNewVOList) {
            orderListVO.setProvinceName(provinceMap.get(orderListVO.getProvince()));
            Map<Long, String> cityMap = dictDistrictBizService.listCity(orderListVO.getProvince()).stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
            orderListVO.setCityName(cityMap.get(orderListVO.getCity()));
        }
        return orderNewVOList;

    }

    /**
     * 公司定向推送
     *
     * @param waitPushForm
     */
    @Transactional
    public void pushCompany(WaitPushForm waitPushForm, LoginUser loginUser) {
        UserBO user = userFacade.queryOne(new UserQuery().setUserId(waitPushForm.getUserId()));
        Assert.notNull(user, "企业下不存在账号");

        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(waitPushForm.getCompanyId()));
        Assert.notNull(companyBO, "企业不存在,请重新选择企业");

        //构建 挂单
        HangingOrderParam hangingOrderParam = new HangingOrderParam().setPriceHighest(waitPushForm.getPrice()).setPriceHighestStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode());
        hangingOrderParam.setPriceHign(Arith.sub(waitPushForm.getPrice(), new BigDecimal(10))).setPriceHignStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode());
        hangingOrderParam.setPriceLow(Arith.sub(hangingOrderParam.getPriceHign(), new BigDecimal(10))).setPriceLowStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode());
        hangingOrderParam.setPriceLowest(Arith.sub(hangingOrderParam.getPriceLow(), new BigDecimal(10))).setPriceLowestStatus(TradeOrderConsts.TradeStatus.CONFIRMED.getCode());
        hangingOrderParam.setQuotationInterval(5L).setAccountingPeriod(waitPushForm.getAccountingPeriod()).setStatus(HandingOrderConsts.Status.NORMAL.getCode());
        hangingOrderParam.setLastCompeteUser(waitPushForm.getUserId()).setLastCompeteCompany(companyBO.getId()).setLastCompeteTime(DateUtil.date()).setCreateBy(loginUser.getUserId());
        hangingOrderParam.setCreateTime(DateUtil.date()).setUpdateTime(DateUtil.date()).setUpdateBy(loginUser.getUserId()).setIntervalSpread(new BigDecimal(10));
        hangingOrderParam.setCodeOptions(HandingOrderConsts.CodeOptions.SEND_BEFORE_NEED.getCode()).setMerchantCompanyId(companyBO.getId());
        hangingOrderParam.setDeliveryTime(waitPushForm.getDeliveryTime()).setDeliveryDeadline(DateUtil.offsetDay(DateUtil.endOfDay(DateUtil.date()), waitPushForm.getDeliveryTime()));

        // 构建trade 对象
        TradeOrderParam tradeOrderParam = new TradeOrderParam().setOrderType(OrderConsts.OrderType.O2O.getCode()).setTradeCompanyId(waitPushForm.getCompanyId());
        tradeOrderParam.setTradeUserId(waitPushForm.getUserId()).setTradeUserPhone(user.getPhone()).setTradeUserName(user.getNickName()).setAccountingPeriod(waitPushForm.getAccountingPeriod());
        tradeOrderParam.setTradeCompanyId(companyBO.getId()).setTradeNickName(companyBO.getNickName()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode());
        tradeOrderParam.setUpdateTime(DateUtil.date()).setUpdateBy(user.getUserId());
        String skuCode = null;
        for (String orderCode : waitPushForm.getOrderCodeList()) {
            OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
            skuCode = Objects.isNull(skuCode) ? orderBO.getSkuCode() : skuCode;
            if (!Objects.equals(skuCode, orderBO.getSkuCode())) {
                throw new ServiceException("请勿推送相同SKU的订单");
            }
            // 删除所有挂单
            hangingOrderFacade.update(new HangingOrderParam().setStatus(HandingOrderConsts.Status.FAILURE.getCode()), new HangingOrderQuery().setOrderId(orderCode).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));

            // 创建 挂单
            HangingOrderBO hangingOrderBO = hangingOrderFacade.save(hangingOrderParam.setOrderId(orderCode));

            // 创建 trade order
            tradeOrderParam.setOrderId(orderCode).setTradePrice(waitPushForm.getPrice()).setBrand(orderBO.getBrand()).setProductName(orderBO.getProductName());
            tradeOrderParam.setSkuName(orderBO.getSkuName()).setSkuCode(orderBO.getSkuCode()).setProvince(orderBO.getProvince()).setQuantity(orderBO.getQuantity());
            tradeOrderParam.setOrderType(orderBO.getOrderType()).setTradeIndex(4).setHangOrderId(hangingOrderBO.getId());
            tradeOrderFacade.save(tradeOrderParam);

            // 订单状态修改
            orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.DELIVERY_ING.getCode()).setSubStatus(OrderConsts.OrderSubStatus.WAIT_IMEI.getCode()).setUpdateTime(DateUtil.date()), new OrderQuery().setOrderCode(orderCode));
        }
    }

    /**
     * 按照原始单号撤销
     *
     * @param revokeOriParam
     */
    public void revokeOriginalList(RevokeOriParam revokeOriParam) {
        for (String originalOrderId : revokeOriParam.getOriginalOrderIdList()) {
            if (StrUtil.isBlank(originalOrderId)) {
                continue;
            }
            List<OrderBO> list = orderFacade.list(new OrderQuery().setOriginalOrderId(originalOrderId));
            if (CollectionUtil.isEmpty(list)) {
                continue;
            }
            for (OrderBO orderBO : list) {
                revoke(orderBO.getOrderCode(), revokeOriParam.getRevokeCode());
            }
        }

    }

    public PageBO<AllOrderVO> allList(AllOrderForm allOrderForm, PageParamV2 pageParamV2) {
        OrderQuery orderQuery = OrderConvert.INSTANCE.allParamToQuery(allOrderForm);
        PageBO<OrderBO> pageBO = orderFacade.listPage(orderQuery, pageParamV2);

        List<AllOrderVO> allOrderVOList = OrderConvert.INSTANCE.toAllOrderVOList(pageBO.getData());
        // 完善省市
        Map<Long, String> provinceMap = dictDistrictBizService.listProvince().stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
        for (AllOrderVO allOrderVO : allOrderVOList) {
            allOrderVO.setProvinceName(provinceMap.get(allOrderVO.getProvince()));
            Map<Long, String> cityMap = dictDistrictBizService.listCity(allOrderVO.getProvince()).stream().collect(Collectors.toMap(DictDistrictBO::getDistrictId, DictDistrictBO::getDistrict));
            allOrderVO.setCityName(cityMap.get(allOrderVO.getCity()));
        }
        List<TradeOrderBO> list = tradeOrderFacade.list(new TradeOrderQuery().setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()).setOrderIdList(pageBO.getData().stream().map(OrderBO::getOrderCode).collect(Collectors.toList())));
        Map<String, TradeOrderBO> tradeMap = list.stream().collect(Collectors.toMap(TradeOrderBO::getOrderId, t -> t));

        for (AllOrderVO orderListVO : allOrderVOList) {
            TradeOrderBO tradeOrderBO = tradeMap.get(orderListVO.getOrderCode());
            if (Objects.isNull(tradeOrderBO)) {
                continue;
            }
            orderListVO.setTradePrice(tradeOrderBO.getTradePrice());
            orderListVO.setCompanyName(tradeOrderBO.getTradeNickName());
        }
        return new PageBO<>(allOrderVOList, pageBO.getTotal());
    }


    public List<AllOrderVO> allListExport(AllOrderForm allOrderForm) {
        List<OrderBO> orderBOList = orderFacade.list(OrderConvert.INSTANCE.allParamToQuery(allOrderForm));

        List<AllOrderVO> allOrderVOList = OrderConvert.INSTANCE.toAllOrderVOList(orderBOList);
        // 完善省市
        List<TradeOrderBO> list = tradeOrderFacade.list(new TradeOrderQuery().setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()).setOrderIdList(allOrderVOList.stream().map(AllOrderVO::getOrderCode).collect(Collectors.toList())));
        Map<String, TradeOrderBO> tradeMap = list.stream().collect(Collectors.toMap(TradeOrderBO::getOrderId, t -> t));

        List<ImeiBO> imeiBOList = imeiFacade.list(new ImeiQuery().setOrderIdList(allOrderVOList.stream().map(AllOrderVO::getOrderCode).collect(Collectors.toList())));
        Map<String, String> imeiMap = imeiBOList.stream().collect(Collectors.groupingBy(ImeiBO::getOrderId, Collectors.mapping(ImeiBO::getImel, Collectors.joining(","))));
        for (AllOrderVO orderListVO : allOrderVOList) {
            TradeOrderBO tradeOrderBO = tradeMap.get(orderListVO.getOrderCode());
            if (Objects.isNull(tradeOrderBO)) {
                continue;
            }
            orderListVO.setTradePrice(tradeOrderBO.getTradePrice());
            orderListVO.setCompanyName(tradeOrderBO.getTradeNickName());
            orderListVO.setImei(imeiMap.get(orderListVO.getOrderCode()));
        }
        return allOrderVOList;
    }

    /**
     * 将全部订单导出到指定文件（用于两步导出模式）
     *
     * @param allOrderForm 查询条件
     * @param file         目标文件
     */
    public void exportExcelToFile(AllOrderForm allOrderForm, File file) {
        List<AllOrderVO> allOrderVOList = allListExport(allOrderForm);
        com.alibaba.excel.EasyExcel.write(file, com.ruoyi.web.vo.order.AllOrderVO.class).sheet("sheet1").doWrite(allOrderVOList);
    }

}


