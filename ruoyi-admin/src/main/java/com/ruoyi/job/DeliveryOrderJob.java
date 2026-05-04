package com.ruoyi.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.weebhook.QWRobotUtil;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.HangingOrderBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.param.TradeOrderParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.query.CompanyQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.ruoyi.consts.DictEnum.WEB_HOOK_CANCELLATION_ORDER;


@Slf4j
@Component("deliveryOrderJob")
public class DeliveryOrderJob {

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    @Autowired
    ICompanyFacade companyFacade;

    /**
     * 发货超时撤销订单
     *      1、判断订单是否是今天发货
     *      2、超时，撤销抢单
     *      3、撤销挂单
     *      4、修改订单状态为待接单
     *      5、发送消息到群聊
     */
    public void execute() {
        log.info("开始执行订单发货撤销定时任务");

        Integer page = 1;
        for (; ; page++) {
            PageParamV2 pageParamV2 = new PageParamV2(page, 100);
            PageBO<OrderBO> orderBOPageBO = orderFacade.listPage(new OrderQuery().setStatus(OrderConsts.OrderStatus.DELIVERY_ING.getCode()), pageParamV2);
            if (CollectionUtil.isEmpty(orderBOPageBO.getData())) {
                break;
            }
            for (OrderBO orderBO : orderBOPageBO.getData()) {

                // 需要判断是否是今天发货
                HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
                if (Objects.isNull(hangingOrderBO)) {
                    continue;
                }
                if (DateUtil.compare(hangingOrderBO.getDeliveryDeadline(), DateUtil.date()) > 0) {
                    log.info("订单还未发货超时：{}", orderBO.getOrderCode());
                    continue;
                }
                // 撤销抢单
                tradeOrderFacade.update(new TradeOrderParam().setStatus(TradeOrderConsts.TradeStatus.TAKEN.getCode()), new TradeOrderQuery()
                        .setOrderId(orderBO.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
                // 撤销挂单
                hangingOrderFacade.update(new HangingOrderParam().setStatus(HandingOrderConsts.Status.FAILURE.getCode()).setUpdateTime(DateUtil.date()),
                        new HangingOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
                // 修改订单
                orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.WAIT.getCode()).setSubStatus(OrderConsts.OrderSubStatus.WAIT_TIMEOUT.getCode())
                        .setUpdateTime(DateUtil.date()), new OrderQuery().setOrderCode(orderBO.getOrderCode()));

                CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(hangingOrderBO.getLastCompeteCompany()));
                // 发送消息到群聊
                String webHook = DictUtils.getDictValue(WEB_HOOK_CANCELLATION_ORDER.getValue(), WEB_HOOK_CANCELLATION_ORDER.getLabel());
                if (StrUtil.isNotBlank(webHook)) {
                    String msg = "**订单已自动毁单，依据：供应商发货超时[" +
                            ">内部单号：<font color='info'>" + orderBO.getOrderCode() + "</font>\r\n" +
                            ">旺店通单号：<font color='info'>" + orderBO.getErpOrderId() + "</font>\r\n" +
                            ">原始单号：<font color='info'>" + orderBO.getOriginalOrderId() + "</font>\r\n" +
                            ">供应商名称：<font color='info'>" + companyBO.getCompanyName() + "</font>\r\n";
                    QWRobotUtil.sendMarkdownMsg(webHook, msg);
                }


            }
        }

        log.info("结束执行订单发货撤销定时任务");
    }

}
