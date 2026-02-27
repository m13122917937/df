package com.ruoyi.biz.order;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.aliyun.openservices.ons.api.Message;
import com.ruoyi.biz.express.bean.ExpressCallbackStatusCode;
import com.ruoyi.biz.mq.MsgClient;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.config.properties.OnsProperties;
import com.ruoyi.consts.WebConstants;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.consts.RouteSubscribeConsts;
import com.ruoyi.express.model.param.RouteSubscribeParam;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.kuaidi100.ExpressClient;
import com.ruoyi.kuaidi100.model.LogisticsOperateType;
import com.ruoyi.kuaidi100.model.SubscribeExpressCode;
import com.ruoyi.kuaidi100.model.SubscribeExpressParam;
import com.ruoyi.kuaidi100.model.SubscribeOperateParam;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.web.form.order.LogisticsSubscribeParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ErrorOrderBizService {

    @Autowired
    MsgClient msgClient;

    @Autowired
    RuoYiConfig ruoYiConfig;

    @Autowired
    OnsProperties onsProperties;

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    ExpressClient expressClient;

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    /**
     * 订阅物流信息并将订单状态从异常修改为在途
     *
     * @return boolean 是否处理成功
     */
    @Transactional
    public boolean subscribeLogisticsAndUpdateOrderStatus(LogisticsSubscribeParam param) {
        log.info("开始处理订单[{}]的物流订阅和状态更新，物流公司[{}]，物流单号[{}]", param.getOrderCode(), param.getLogisticsCode(), param.getLogisticsNo());

        // 1. 检查订单是否存在且状态为异常
        OrderBO order = orderFacade.getOne(new OrderQuery().setOrderCode(param.getOrderCode()));
        Assert.notNull(order, "订单不存在");
        log.info("订单[{}]状态为异常，开始处理物流订阅和状态更新, {}", param.getOrderCode(), order.getStatus());
        // 检查订单状态为异常
        Assert.isTrue(OrderConsts.OrderStatus.ERROR.getCode().equals(order.getStatus()), "订单状态异常，请刷新页面");

        // 2. 查询物流订阅记录，取消订阅,并且删除本地记录
        RouteSubscribeBO existingSubscribe = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setOrderCode(param.getOrderCode()));
        if(Objects.equals(existingSubscribe.getPhone(), param.getPhone())){
            throw new ServiceException("当前手机号码已订阅过该订单的物流信息，请勿重复订阅");
        }
        routeSubscribeFacade.remove(new RouteSubscribeQuery().setOrderCode(param.getOrderCode()));
        // 3. 保存快递信息
        RouteSubscribeParam subscribeParam = new RouteSubscribeParam();
        subscribeParam.setOrderCode(param.getOrderCode()).setLogisticsCode(param.getLogisticsCode()).setLogisticsNo(param.getLogisticsNo())
                .setStatus(RouteSubscribeConsts.Status.subscribe.getValue()).setTraceStatus(ExpressCallbackStatusCode.POLLING_STATUS.getCode())
                .setCreateTime(DateUtil.date()).setUpdateTime(DateUtil.date()).setPhone(param.getPhone());
        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.save(subscribeParam);

        // 4.调用快递100接口订阅物流信息
        subscribeExpress(routeSubscribeBO);

        // 5. 订阅成功后，将订单状态从异常修改为在途
        orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.TRANSIT.getCode()).setUpdateTime(DateUtil.date()), new OrderQuery().setOrderCode(param.getOrderCode()));

        log.info("订单[{}]物流订阅成功，状态已从异常修改为在途", param.getOrderCode());
        return true;
    }

    private void subscribeExpress(RouteSubscribeBO routeSubscribeBO) {

        SubscribeExpressCode subscribeExpressCode = expressClient.subscribeExpress(new SubscribeExpressParam().setExpressNo(routeSubscribeBO.getLogisticsNo())
                .setExpressCode(routeSubscribeBO.getLogisticsCode()).setOrderId(routeSubscribeBO.getOrderCode()).setCellphone(routeSubscribeBO.getPhone())
                .setExpressCallBackUrl(String.format(WebConstants.EXPRESS_NOTIFY_URL, ruoYiConfig.getHost())));

        log.info("订单[{}]物流订阅成功，状态已从异常修改为在途,快递单号：{}，手机号码：{},订阅状态：{}", routeSubscribeBO.getOrderCode(), routeSubscribeBO.getLogisticsNo(), routeSubscribeBO.getPhone(), subscribeExpressCode.getMsg());

    }

}
