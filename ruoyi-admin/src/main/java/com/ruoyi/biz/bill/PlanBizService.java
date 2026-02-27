package com.ruoyi.biz.bill;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.bill.constant.BillConsts;
import com.ruoyi.bill.constant.BillPayPlanConsts;
import com.ruoyi.bill.constant.TransactionsConsts;
import com.ruoyi.bill.facade.IBillFacade;
import com.ruoyi.bill.facade.IBillPayPlanFacade;
import com.ruoyi.bill.facade.ITransactionsFacade;
import com.ruoyi.bill.model.bo.BillPayPlanBO;
import com.ruoyi.bill.model.param.BillParam;
import com.ruoyi.bill.model.param.BillPayPlanParam;
import com.ruoyi.bill.model.param.TransactionsParam;
import com.ruoyi.bill.model.query.BillPayPlanQuery;
import com.ruoyi.bill.model.query.BillQuery;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.mapper.bill.BillPlanConvert;
import com.ruoyi.web.form.bill.PlanForm;
import com.ruoyi.web.vo.bill.BillPayPlanVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
public class PlanBizService {

    @Autowired
    IBillPayPlanFacade billPayPlanFacade;

    @Autowired
    IBillFacade billFacade;

    @Autowired
    TransactionsBizService transactionsBizService;


    public BillPayPlanVO plan(Long planId) {

        BillPayPlanBO billPayPlanBO = billPayPlanFacade.getOne(new BillPayPlanQuery().setId(planId));
        return BillPlanConvert.INSTANCE.toVo(billPayPlanBO);
    }

    /**
     * 排款修改订单状态
     *
     * @param planForm
     */
    @Transactional
    public void update(PlanForm planForm, Long userId) {
        log.info("排款修改订单状态:{}, 用户id：{}", planForm, userId);
        DateTime now = DateUtil.date();
        BillPayPlanBO billPayPlanBO = billPayPlanFacade.getOne(new BillPayPlanQuery().setId(planForm.getId()));

        // 更新排款计划
        BillPayPlanParam payPlanParam = BillPlanConvert.INSTANCE.toParam(planForm).setPayTime(DateUtil.date()).setUpdateBy(userId).setUpdateTime(now).setStatus(BillPayPlanConsts.Status.WAIT_CONFIRM.getCode());
        billPayPlanFacade.update(payPlanParam, new BillPayPlanQuery().setId(planForm.getId()));
        // 更新账单记录
        billFacade.update(BillParam.builder().status(BillConsts.BillPayStatus.UNCONFIRMED_STATUS.getCode()).payDate(now).updateBy(userId).updateTime(now).build()
                , new BillQuery().setPayPlan(BillPayPlanConsts.PayPlan.PAYMENT.getCode()).setPlanId(planForm.getId()));
        // 添加资金流水记录
        if (Objects.equals(billPayPlanBO.getStatus(), BillPayPlanConsts.Status.WAIT_PAY.getCode())) {
            TransactionsParam transactionsParam = TransactionsParam.builder().accountId(billPayPlanBO.getPayCompanyId()).updatedAt(DateUtil.date())
                    .amount(billPayPlanBO.getPayAmount()).transactionDate(DateUtil.date()).counterparty(billPayPlanBO.getSupplierName())
                    .createdAt(DateUtil.date()).category(TransactionsConsts.Category.EXPENSE.getCode()).subCategory(TransactionsConsts.SubCategory.OUT_PAY.getName())
                    .paymentMethod("转账").remark("货款").build();
            transactionsBizService.addTransactionAndUpdateBalance(transactionsParam, userId);
        }

    }
}
