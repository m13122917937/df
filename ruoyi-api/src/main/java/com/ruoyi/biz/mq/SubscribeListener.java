package com.ruoyi.biz.mq;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.consts.WebConstants;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.kuaidi100.ExpressClient;
import com.ruoyi.kuaidi100.model.SubscribeExpressCode;
import com.ruoyi.kuaidi100.model.SubscribeExpressParam;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.HangingOrderBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.param.TradeOrderParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Slf4j
@Getter
@Component
public class SubscribeListener implements MessageListener {

    @Autowired
    RuoYiConfig ruoYiConfig;
    @Autowired
    private ExpressClient expressClient;
    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @Override
    @Transactional
    public Action consume(Message message, ConsumeContext context) {
        long routeSubscribeId = Convert.bytesToLong(message.getBody());
        //
        log.info("订阅到消息:{}", routeSubscribeId);
        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setId(routeSubscribeId));
        if (Objects.isNull(routeSubscribeBO)) {
            log.error("物流订阅记录不存在，id:{}", routeSubscribeId);
            return Action.CommitMessage;
        }
        // 订阅物流信息
        SubscribeExpressCode subscribeExpressCode = expressClient.subscribeExpress(new SubscribeExpressParam().setExpressNo(routeSubscribeBO.getLogisticsNo())
                .setExpressCode(routeSubscribeBO.getLogisticsCode()).setOrderId(routeSubscribeBO.getOrderCode()).setCellphone(routeSubscribeBO.getPhone())
                .setExpressCallBackUrl(String.format(WebConstants.EXPRESS_NOTIFY_URL, ruoYiConfig.getHost())));
        log.info("订单号：{}，订阅物流信息结果:{}", routeSubscribeBO.getOrderCode(), subscribeExpressCode);
        return Action.CommitMessage;
    }


}
