package com.ruoyi.biz.express;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.biz.express.bean.ExpressUtils;
import com.ruoyi.biz.express.bean.ShippedErrorStatus;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.order.facade.IApplyFacade;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.user.facade.ICompanyFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author nlsm
 * 物流状态变化抽象类
 */
@Slf4j
public abstract class AbstractLogisticsState implements LogisticsState {

    /** 放行子状态 */
//    private static final int[] SUB_STATUS = {OrderSubStatus.LOGISTICS_NO_FLOW.getCode(),
//            OrderSubStatus.NO_SIGN_DAYS.getCode(), OrderSubStatus.LOGISTICS_PHONE_SUFFIX.getCode()};
    /**
     * 扣除金额
     */
    private static final BigDecimal AMOUNT = new BigDecimal(-200);

    @Resource
    protected IOrderFacade orderFacade;
    @Resource
    protected IApplyFacade applyFacade;
    @Resource
    protected ITradeOrderFacade tradeOrderFacade;
    @Resource
    private IHangingOrderFacade hangingOrderFacade;
    @Resource
    private ICompanyFacade companyFacade;
    @Autowired
    protected IRouteSubscribeFacade routeSubscribeFacade;

//    /**
//     * 转异常方法
//     *
//     * @param orderId     订单主键
//     * @param logisticsNo 物流单号
//     */
//    protected final void abnormalDeductionCapital(Long orderId, String logisticsNo) {
//        log.info("修改转售后时间orderId={},no={}", orderId, logisticsNo);
//        supplierOrderBizService.abnormalDeductionCapital(orderId);
//    }

//    /**
//     * 判断是否有物流申请还未处理
//     *
//     * @param orderCode 订单主键
//     * @param no        快递单号
//     * @return true：有物流申请未处理  false：没有物流申请
//     */
//    protected final Boolean isAlterLogisticsApply(String orderCode, String no) {
//        List<ApplyBO> applyBoList = applyFacade.list(new ApplyQuery().setOrderId(orderCode));
//        if (CollUtil.isNotEmpty(applyBoList)) {
//            return false;
//        }
//        for (ApplyBO applyBo : applyBoList) {
//            if (Objects.equals(ApplyType.CANCEL_ORDER.getCode(), applyBo.getType()) && Objects.equals(ApplyStatus.IN_PASS.getCode(), applyBo.getStatus())) {
//                log.info("订单有取消订单申请，作废申请-->orderId={}, no={}", orderId, no);
//                ApplyParam param = new ApplyParam();
//                param.setStatus(ApplyStatus.NO_PASS.getCode());
//                param.setRemark("快递订阅签收-自动拒绝");
//                applyService.update(param, ApplyQueryParam.builder().id(applyBo.getId()).build());
//                // 修改订单申请状态
//                orderService.updateHandleApply(orderId, false);
//            }
//            if (Objects.equals(ApplyType.LOGISTICS_ERROR.getCode(), applyBo.getType())) {
//                log.info("订单有修改物流申请，不处理-->orderId={}, no={}", orderId, no);
//                return true;
//            }
//        }
//        return false;
//    }

//    /**
//     * 判断是否有物流申请
//     *
//     * @param orderId 订单号
//     * @return 是否存在
//     */
//    protected boolean expressApply(String orderId) {
//        List<ApplyBO> applyBoList = applyFacade.list(new ApplyQuery().setOrderId((orderId)));
//        if (CollUtil.isEmpty(applyBoList)) {
//            return false;
//        }
//        // 有申请
//        for (ApplyBO applyBo : applyBoList) {
//            if (Objects.equals(ApplyType.LOGISTICS_ERROR.getCode(), applyBo.getType())
//                    && Objects.equals(ApplyStatus.IN_PASS.getCode(), applyBo.getStatus())) {
//                // 物流修改申请 并且 是申请中 不处理
//                return true;
//            }
//        }
//        return false;
//    }

//    /**
//     * 作废申请
//     *
//     * @param orderCode 订单主键
//     * @param no        快递单号
//     */
//    protected final void clearApply(String orderCode, String no) {
//        List<ApplyBO> applyBoList = applyFacade.list(new ApplyQuery().setOrderId(orderCode));
//        if (CollUtil.isEmpty(applyBoList)) {
//            return;
//        }
//        for (ApplyBO applyBo : applyBoList) {
//            if (Objects.equals(ApplyStatus.IN_PASS.getCode(), applyBo.getStatus())) {
//                log.info("订单有取消订单申请，作废申请-->orderId={}, no={}", orderId, no);
//                ApplyParam param = new ApplyParam();
//                param.setStatus(ApplyStatus.NO_PASS.getCode());
//                param.setRemark("快递订阅-有物流轨迹");
//                applyService.update(param, ApplyQueryParam.builder().id(applyBo.getId()).build());
//            }
//        }
//        // 修改订单申请状态
//        orderService.updateHandleApply(orderId, false);
//    }

    /**
     * 根据异常状态修改订单信息
     *
     * @param orderCode 订单主键
     * @param express   异常信息
     */
    protected final void updateLogisticsStatus(String orderCode, ShippedErrorStatus express) {
        if (Objects.equals(ShippedErrorStatus.NO_ERROR.getCode(), express.getCode())) {
            return;
        }
        //修改订单状态
        switch (express) {
            case TO_ERROR:
                //物流目的地异常
//                orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.ERROR.getCode())
//                        .setSubStatus(OrderConsts.OrderSubStatus.ERROR_LOGISTICS_TO_ERROR.getCode()), new OrderQuery().setOrderCode(orderCode));
                break;
            case FROM_ERROR:
                //物流发货城市异常
//                orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.ERROR.getCode())
//                        .setSubStatus(OrderConsts.OrderSubStatus.ERROR_LOGISTICS_FROM_ERROR.getCode()), new OrderQuery().setOrderCode(orderCode));
                break;
            case NO_EXCHANGE_ERROR:
                //物流无流转信息
                //物流发货城市异常
//                orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.ERROR.getCode())
//                        .setSubStatus(OrderConsts.OrderSubStatus.ERROR_LOGISTICS_CIRCULATION.getCode()), new OrderQuery().setOrderCode(orderCode));
                break;
            case NO_SIGN_ERROR:
                //签收异常
                orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.ERROR.getCode())
                        .setSubStatus(OrderConsts.OrderSubStatus.ERROR_LOGISTICS_SIGN.getCode()), new OrderQuery().setOrderCode(orderCode));
                break;
            case KNOTTY_ERROR:
                //快递疑难异常
//                orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.ERROR.getCode())
//                        .setSubStatus(OrderConsts.OrderSubStatus.ERROR_LOGISTICS_FROM_KNOTTY.getCode()), new OrderQuery().setOrderCode(orderCode));
                break;
            case PHONE_ERROR:
                //手机号异常
                orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.ERROR.getCode())
                        .setSubStatus(OrderConsts.OrderSubStatus.ERROR_LOGISTICS_PHONE.getCode()), new OrderQuery().setOrderCode(orderCode));
                break;
            default:
                break;
        }
    }

    /**
     * 查询订单数据
     *
     * @param orderId 订单主键
     * @return BO
     */
    protected final OrderBO getOrder(String orderId) {
        return orderFacade.getOne(new OrderQuery().setOrderCode(orderId));
    }


    /**
     * 判断揽收时间是否超过24小时
     *
     * @param orderBo     订单信息
     * @param collectDate 揽收时间
     * @param logisticsNo 物流单号
     */
    protected void collectTimeout(OrderBO orderBo, Date collectDate, String logisticsNo) {
//        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setLogisticsNo(logisticsNo));
//        if (Objects.isNull(routeSubscribeBO)) {
//            log.info("未查询此订单的物流信息orderId={},no={}", orderBo.getOrderCode(), logisticsNo);
//            return;
//        }
        if (Objects.nonNull(orderBo.getShipmentsTime())) {
            return;
        }

        // 判断是否揽收时间超时
        if (ExpressUtils.collectTimeout(orderBo.getSendTime(), collectDate)) {
            //TODO 设置 超时扣罚订单


//            expressOrderBo.setCollectTimeout(true);
//            expressOrderService.updateById(ExpressOrderConvert.INSTANCE.toUpdateParam(expressOrderBo));
//            // 超时增加扣罚订单
//            TradeOrderBO tradeOrderBo = tradeOrderService.getTradePrice(orderBo.getId(), TradeOrderStatus.CLOSED.getCode());
//            HangingOrderBO hangingOrderBo = hangingOrderService.getOne(HangOrderQueryParam.builder().orderId(orderBo.getId()).status(HangOrderStatus.EFFICIENT.getCode())
//                    .newest(NewestStatus.YES.getCode()).build());
//            // 超时生成扣罚订单
//            Boolean abatement = this.buildAbatement(orderBo, tradeOrderBo, hangingOrderBo);
//            if (abatement) {
//                String uncollectedKey = DictUtils.getDictValue(ApiConsts.DictKey.Type.WEBHOOK, ApiConsts.DictKey.Label.EXPRESS_UNCOLLECTED);
//                if (StrUtil.isNotBlank(uncollectedKey)) {
//                    return;
//                }
//                String sb = "**扣罚提醒-" + "<font color='warning'>" + "揽收时间超过24小时" + "</font>**\r\n" +
//                        ">原始单号：<font color='info'>" + orderBo.getOriginalOrderId() + "</font>\r\n" +
//                        ">无仓单号：<font color='info'>" + orderBo.getOrderCode() + "</font>\r\n" +
//                        ">是否已自动退货追单：<font color='info'>" + "否" + "</font>\r\n";
//                QWRobotUtil.sendMarkdownMsg(uncollectedKey, sb);
//            }
        } else {
            // 未超时
            orderFacade.update(new OrderParam().setShipmentsTime(collectDate), new OrderQuery().setOrderCode(orderBo.getOrderCode()));
        }
    }


//    /**
//     * 获取企业名称
//     *
//     * @param companyId 企业主键
//     * @return 企业名称
//     */
//    protected final String getCompanyName(Long companyId) {
//        CompanyBO company = companyFacade.queryOne(new CompanyQuery().setId(companyId));
//        if (Objects.isNull(company)) {
//            return null;
//        }
//        return company.getCompanyName();
//    }

    /**
     * 判断揽收时间是否小于成交时间
     *
     * @param orderBo     订单数据
     * @param collectDate 揽收时间
     * @param logisticsNo 物流单号
     * @return true揽收时间小于等于匹配时间 false 揽收时间大于匹配时间
     */
    protected boolean isBeforeCollect(OrderBO orderBo, Date collectDate, String logisticsNo) {
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBo.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        if (Objects.isNull(tradeOrderBO)) {
            return false;
        }
        return DateUtil.compare(tradeOrderBO.getCreateTime(), collectDate) > 0;
    }

}
