package com.ruoyi.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.biz.bill.DeductionBizService;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.kuaidi100.ExpressClient;
import com.ruoyi.kuaidi100.model.ExpressResult;
import com.ruoyi.kuaidi100.model.consts.LogisticsStatus;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.web.form.bill.DeductionSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


@Slf4j
@Component("expressErrorJob")
public class ExpressErrorJob {

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    ExpressClient expressClient;

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @Autowired
    DeductionBizService deductionBizService;


    /**
     * 每天晚上11.50执行
     * <p>
     * 物流无流转，转手机号异常
     *
     * @throws InterruptedException
     */
    public void execute() throws InterruptedException {
        DateTime now = DateUtil.date();
        List<OrderBO> orderList = orderFacade.list(new OrderQuery().setStatus(OrderConsts.OrderStatus.TRANSIT.getCode())
                .setOrderType(OrderConsts.OrderType.O2O.getCode()).setCreateTimeStart(DateUtil.offsetDay(now, -30)));
        if (CollectionUtil.isEmpty(orderList)) {
            log.info("暂无订单需要更新物流信息...");
            return;
        }
        //
        for (OrderBO orderBO : orderList) {
            try {
                // 去除已经签收的订单
                if (Objects.nonNull(orderBO.getSignedTime())) {
                    continue;
                }
                if (Objects.nonNull(orderBO.getShipmentsTime())) {
                    continue;
                }
                // 未揽收，判断是否手机号码异常
                RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setOrderCode(orderBO.getOrderCode()));
                if (Objects.isNull(routeSubscribeBO)) {
                    continue;
                }
                // 接口查询一下订单物流状态
                ExpressResult express = expressClient.findExpress(routeSubscribeBO.getLogisticsCode(), routeSubscribeBO.getLogisticsNo(), routeSubscribeBO.getPhone());
                if (Objects.equals(express.getReturnCode(), 408)) {
                    //  判断是否是手机号码异常
//                        if (LogisticsStatus.phone(express.getState())) {
                    log.warn("订单号：{}，手机号码异常，转手机号码异常中", orderBO.getOrderCode());
                    orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.ERROR.getCode()).setUpdateTime(DateUtil.date())
                                    .setSubStatus(OrderConsts.OrderSubStatus.ERROR_LOGISTICS_PHONE.getCode())
                            , new OrderQuery().setOrderCode(orderBO.getOrderCode()));
//                        }
                } else {
                    if (DateUtil.between(orderBO.getSendTime(), now, DateUnit.HOUR) < 24) {
                        continue;
                    }
//                    log.warn("订单号：{}，物流无流转异常，转物流异常中", orderBO.getOrderCode());
//                    orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.ERROR.getCode()).setUpdateTime(DateUtil.date()).setSubStatus(OrderConsts.OrderSubStatus.ERROR_LOGISTICS_CIRCULATION.getCode())
//                            , new OrderQuery().setOrderCode(orderBO.getOrderCode()));
//                    // 生成扣罚订单
//                    DeductionSaveForm deductionSaveForm = new DeductionSaveForm();
//                    deductionSaveForm.setOrderCode(orderBO.getOrderCode());
//                    deductionSaveForm.setAmount(new BigDecimal(200));
//                    deductionSaveForm.setRemark(String.format("订单%s,无物流流转，扣罚200元", orderBO.getOrderCode()));
//                    LoginUser loginUser = new LoginUser();
//                    loginUser.setUserId(1L);
//                    deductionBizService.save(deductionSaveForm, loginUser);
                }
            } catch (Exception e) {
                log.error("订单物流判断异常：", e);
            }
        }


    }
}
