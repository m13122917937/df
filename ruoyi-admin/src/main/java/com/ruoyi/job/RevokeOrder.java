package com.ruoyi.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.param.reject.RejectParam;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.OrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("revokeOrder")
public class RevokeOrder {
    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    @Autowired
    JkyTemplate jkyTemplate;

    /**
     * 每天晚上7点撤销全部订单任务
     *
     * 1、删除挂单
     * 2、调用吉客云驳回接口
     * 3、修改订单状态
     */
    public void execute() {
        log.info("开始执行订单撤销定时任务");

        Integer page = 1;
        for (; ; page++) {
            PageParamV2 pageParamV2 = new PageParamV2(page, 100);
            PageBO<OrderBO> orderBOPageBO = orderFacade.listPage(new OrderQuery().setStatusList(List.of(OrderConsts.OrderStatus.NEW.getCode(), OrderConsts.OrderStatus.WAIT.getCode())), pageParamV2);
            if (CollectionUtil.isEmpty(orderBOPageBO.getData())) {
                break;
            }
            for (OrderBO orderBO : orderBOPageBO.getData()) {
                // 删除挂单
                hangingOrderFacade.update(new HangingOrderParam().setStatus(HandingOrderConsts.Status.FAILURE.getCode()).setUpdateTime(DateUtil.date()),
                        new HangingOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
                // 代发订单调用吉客云驳回接口
                rejectJkyIfNeeded(orderBO);
                // 设置订单到撤销中
                orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.REVOKE.getCode()).setSubStatus(OrderConsts.OrderSubStatus.REVOKE_DELISTING.getCode()).setUpdateTime(DateUtil.date()),
                        new OrderQuery().setOrderCode(orderBO.getOrderCode()));

            }
        }
        log.info("结束执行订单撤销定时任务");
    }

    /**
     * 代发订单吉客云驳回
     */
    private void rejectJkyIfNeeded(OrderBO orderBO) {
        if (Objects.nonNull(orderBO)
                && Objects.equals(orderBO.getOrderType(), OrderConsts.OrderType.O2O.getCode())) {
            String tradeNo = StrUtil.blankToDefault(orderBO.getJkyTradeNo(), orderBO.getErpOrderId());
            if (StrUtil.isBlank(tradeNo)) {
                return;
            }
            RejectParam rejectParam = new RejectParam();
            rejectParam.setTradeNos(Collections.singletonList(orderBO.getJkyTradeNo()));
            jkyTemplate.reject(rejectParam);
        }
    }
}
