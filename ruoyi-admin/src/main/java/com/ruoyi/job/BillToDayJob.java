package com.ruoyi.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.bill.constant.BillConsts;
import com.ruoyi.bill.facade.IBillFacade;
import com.ruoyi.bill.facade.IBillPayPlanFacade;
import com.ruoyi.bill.model.bo.BillPayPlanBO;
import com.ruoyi.bill.model.param.BillParam;
import com.ruoyi.bill.model.param.BillPayPlanParam;
import com.ruoyi.bill.model.query.BillPayPlanQuery;
import com.ruoyi.bill.model.query.BillQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 财务付款定时任务
 * <p>
 * 设置今天付款的账单为显示
 */
@Slf4j
@Component("billToDayJob")
public class BillToDayJob {

    @Autowired
    IBillFacade billFacade;

    @Autowired
    IBillPayPlanFacade payPlanFacade;

    @Transactional
    public void execute() {
        log.info("开始执行财务付款定时任务");

        // 处理昨日排款未付款的账单
//        List<BillPayPlanBO> list = payPlanFacade.list(new BillPayPlanQuery().setStatus(BillConsts.BillPayStatus.ABNORMAL_STATUS.getCode()), null);
//        if (CollectionUtil.isNotEmpty(list)) {
//            for (BillPayPlanBO billPayPlanBO : list) {
//                log.warn("排款id：{}，未付款，回滚数据, 供应商名称：{}，未付款金额：{}", billPayPlanBO.getId(), billPayPlanBO.getSupplierName(), billPayPlanBO.getPayAmount());
//                //
//                payPlanFacade.update(new BillPayPlanParam().setStatus(BillConsts.BillPayStatus.FREEZE.getCode()), new BillPayPlanQuery().setId(billPayPlanBO.getId()).setStatus(BillConsts.BillPayStatus.NO_PAY_STATUS.getCode()));
//                //
//                billFacade.update(BillParam.builder().payPlan(BillConsts.BillPayStatus.NO_PAY_STATUS.getCode()).build(), new BillQuery().setPlanId(billPayPlanBO.getId()));
//
//            }
//        }
        // 处理今天已经打款订单
        billFacade.update(BillParam.builder().todayShow(BillConsts.TodayShow.NOT_SHOW.getCode()).build(),
                new BillQuery().setTodayShow(BillConsts.TodayShow.SHOW.getCode()).setStatusList(
                        List.of(BillConsts.BillPayStatus.UNCONFIRMED_STATUS.getCode(), BillConsts.BillPayStatus.CONFIRMED_STATUS.getCode(),BillConsts.BillPayStatus.ERROR_STATUS.getCode(),
                                        BillConsts.BillPayStatus.ABNORMAL_STATUS.getCode(), BillConsts.BillPayStatus.STOP_PAY.getCode(), BillConsts.BillPayStatus.FREEZE.getCode()))
        );
        // 处理到期订单
        billFacade.update(BillParam.builder().todayShow(BillConsts.TodayShow.SHOW.getCode()).build(),
                new BillQuery().setTodayShow(BillConsts.TodayShow.NOT_SHOW.getCode()).setSettlementDate(LocalDate.now()));


        log.info("结束执行财务付款定时任务");
    }


}
