
package com.ruoyi.biz.order;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.bill.constant.BillConsts;
import com.ruoyi.bill.constant.BillPayPlanConsts;
import com.ruoyi.bill.facade.IBillFacade;
import com.ruoyi.bill.model.bo.BillBO;
import com.ruoyi.bill.model.param.BillParam;
import com.ruoyi.bill.model.query.BillQuery;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.mapper.bill.BillConvert;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.web.form.order.AftersalesForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Component
public class AfterSalesBizService {

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    IBillFacade billFacade;

    /**
     * 添加售后订单
     *
     * @param aftersalesForm
     */
    @Transactional
    public void add(AftersalesForm aftersalesForm, LoginUser loginUser) {

        for (String orderCode : aftersalesForm.getOrderCodeSet()) {
            log.info("开始售后订单：{}, 操作用户：{}", orderCode, loginUser.getUserId());
            OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
            if (Objects.isNull(orderBO)) {
                log.info("订单号不存在，不能售后：{}", orderCode);
                continue;
            }
            if (!Objects.equals(orderBO.getStatus(), OrderConsts.OrderStatus.ENDING.getCode())) {
                log.info("订单号非完成状态，不能售后：{}", orderCode);
                continue;
            }
            boolean update = orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.AFTER_SALES.getCode()).setSubStatus(OrderConsts.OrderSubStatus.AFTER_SALES_SEND_REFUND.getCode())
                            .setUpdateTime(DateUtil.date()), new OrderQuery().setOrderCode(orderCode));
            if (!update) {
                log.info("售后订单添加失败：{}", orderCode);
                continue;
            }
            //添加财务负账单
            BillBO billBO = billFacade.getOne(new BillQuery().setOrderCode(orderCode).setReversed(BillConsts.BillReversedType.FORWARD_DIRECTION.getCode()));
            if (Objects.isNull(billBO)) {
                log.info("售后订单未生成财务账单，不能售后：{}", orderCode);
                continue;
            }
            // 生成财务负账单
            BillParam billParam = BillConvert.INSTANCE.toBillParam(billBO);
            billParam.setPlanId(null).setPayPlan(BillPayPlanConsts.PayPlan.NOT_PAYMENT.getCode()).setSettlementDate(LocalDate.now().plusDays(1))
                    .setQuantity(-billParam.getQuantity()).setBillingAmount(billParam.getBillingAmount().negate()).setTradePrice(billParam.getTradePrice().negate())
                    .setUpdateTime(DateUtil.date()).setUpdateBy(loginUser.getUserId()).setId(null);
            billFacade.save(billParam);

        }
    }
}
