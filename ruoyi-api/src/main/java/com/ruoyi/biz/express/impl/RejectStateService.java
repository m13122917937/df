package com.ruoyi.biz.express.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.biz.express.AbstractLogisticsState;
import com.ruoyi.biz.express.LogisticsState;
import com.ruoyi.biz.express.bean.LogisticsStateParam;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.weebhook.QWRobotUtil;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ruoyi.consts.DictEnum.WEB_HOOK_FOLLOW_ORDER;

/**
 * @author nlsm
 * 退签状态逻辑处理
 */
@Slf4j
@Service
public class RejectStateService extends AbstractLogisticsState implements LogisticsState {

    /**
     * 可以自动退货追单的订单状态
     */
    private final static int[] AUTO_SALES_STATUS = {OrderConsts.OrderStatus.DELIVERY_END.getCode(), OrderConsts.OrderStatus.TRANSIT.getCode()};


    @Override
    public void doAction(LogisticsStateParam stateParam) {
        String orderId = stateParam.getOrderId();
        log.info("物流签收异常---orderId={}", orderId);
        // 提醒供应商退回快件是否签收, 需要供应商核对是否有问题
        OrderBO orderBO = super.getOrder(orderId);

        // 判断是否需要自动退货追单 并且包含 系统可以自动退货追单的状态
        if (stateParam.getSalesReturn() && ArrayUtil.contains(AUTO_SALES_STATUS, orderBO.getStatus())) {
            log.info("物流退回，系统自动退货追单 orderId={},no={}", orderId, stateParam.getLogisticsNo());
            orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.CHASE_ORDER.getCode()).setSubStatus(OrderConsts.OrderSubStatus.DELIVERY_END_BACK.getCode())
                    .setUpdateTime(DateUtil.date()), new OrderQuery().setOrderCode(orderId));
            sendMessage(stateParam.getLogisticsNo(), orderBO);
            return;
        }
    }

    /**
     * 发送消息到企业微信
     *
     * @param logisticsNo 物流单号
     * @param orderBo     订单信息
     */
    private void sendMessage(String logisticsNo, OrderBO orderBo) {
        String uncollectedKey = DictUtils.getDictValue(WEB_HOOK_FOLLOW_ORDER.getValue(), WEB_HOOK_FOLLOW_ORDER.getLabel());
        if (StrUtil.isNotBlank(uncollectedKey)) {
            String sb = "**订单已自动退货追单，依据：物流节点中包含[" + "<font color='warning'>应客户要求,快件正在退回中</font>]**\r\n" +
                    ">内部单号：<font color='info'>" + orderBo.getOrderCode() + "</font>\r\n" +
                    ">旺店通单号：<font color='info'>" + orderBo.getErpOrderId() + "</font>\r\n" +
                    ">原始单号：<font color='info'>" + orderBo.getOriginalOrderId() + "</font>\r\n" +
                    ">物流单号：<font color='info'>" + logisticsNo + "</font>\r\n";
            QWRobotUtil.sendMarkdownMsg(uncollectedKey, sb);
        }
    }
}
