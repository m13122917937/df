package com.ruoyi.biz.bill;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.bill.constant.BillConsts;
import com.ruoyi.bill.constant.BillPayPlanConsts;
import com.ruoyi.bill.facade.IBillFacade;
import com.ruoyi.bill.facade.IBillPayPlanFacade;
import com.ruoyi.bill.facade.IPayerConfigFacade;
import com.ruoyi.bill.facade.IPayerFacade;
import com.ruoyi.bill.model.bo.BillBO;
import com.ruoyi.bill.model.bo.BillPayPlanBO;
import com.ruoyi.bill.model.bo.PayerBO;
import com.ruoyi.bill.model.bo.PayerConfigBO;
import com.ruoyi.bill.model.param.BillParam;
import com.ruoyi.bill.model.param.BillPayPlanParam;
import com.ruoyi.bill.model.query.BillPayPlanQuery;
import com.ruoyi.bill.model.query.BillQuery;
import com.ruoyi.bill.model.query.PayerConfigQuery;
import com.ruoyi.bill.model.query.PayerQuery;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.Arith;
import com.ruoyi.mapper.bill.BillConvert;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.HangingOrderBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.user.facade.ICompanyBankFacade;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.bo.CompanyBankBO;
import com.ruoyi.user.model.consts.CompanyBankConsts;
import com.ruoyi.user.model.query.CompanyBankQuery;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.web.form.bill.BillForm;
import com.ruoyi.web.vo.bill.BillExportVO;
import com.ruoyi.web.vo.bill.TodayBillPayPlanVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BillBizService {

    @Autowired
    IBillPayPlanFacade billPayPlanFacade;

    @Autowired
    IBillFacade billFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    @Autowired
    IPayerConfigFacade payerConfigFacade;

    @Autowired
    IPayerFacade payerFacade;

    @Autowired
    ICompanyFacade companyFacade;

    @Autowired
    ICompanyBankFacade companyBankFacade;

    /**
     * 生成财务账单
     *
     * @param orderBO
     */
    @Transactional
    public void generateBill(OrderBO orderBO) {
        if (Objects.isNull(orderBO)) {
            return;
        }
        log.info("开始生成订单账单：{}", orderBO.getOrderCode());
        HangingOrderBO hangingOrderBO = hangingOrderFacade.getOne(new HangingOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(HandingOrderConsts.Status.NORMAL.getCode()));
        if (Objects.isNull(hangingOrderBO)) {
            log.info("订单{}无挂单", orderBO.getOrderCode());
            return;
        }
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBO.getOrderCode()).setHangOrderId(hangingOrderBO.getId()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        if (Objects.isNull(tradeOrderBO)) {
            log.info("订单{}无成交订单", orderBO.getOrderCode());
            return;
        }
        PayerConfigBO payerConfigBO = payerConfigFacade.getOne(new PayerConfigQuery().setKeyWord(orderBO.getShopName()));
        if (Objects.isNull(payerConfigBO)) {
            payerConfigBO = payerConfigFacade.listPage(new PayerConfigQuery(), new PageParamV2()).getData().get(0);
        }
        CompanyBankBO companyBankBO = companyBankFacade.getOne(new CompanyBankQuery().setDefaulted(CompanyBankConsts.Defaulted.YES.getValue()).setCompanyId(tradeOrderBO.getTradeCompanyId()));

        // 计算结算时间
        BillParam billParam = BillParam.builder().orderCode(orderBO.getOrderCode()).originalOrderId(orderBO.getOriginalOrderId()).billType(BillConsts.BillType.ONE_ITEM_SEND.getCode()).reversed(BillConsts.BillReversedType.FORWARD_DIRECTION.getCode())
                .brand(orderBO.getBrand()).category(orderBO.getCategory()).productName(orderBO.getProductName()).skuName(orderBO.getSkuName()).skuCode(orderBO.getSkuCode())
                .quantity(orderBO.getQuantity()).tradePrice(tradeOrderBO.getTradePrice()).billingAmount(Arith.mul(tradeOrderBO.getTradePrice(), new BigDecimal(orderBO.getQuantity())))
                .payPlan(BillPayPlanConsts.PayPlan.NOT_PAYMENT.getCode()).status(BillConsts.BillPayStatus.NO_PAY_STATUS.getCode()).accounting(hangingOrderBO.getAccountingPeriod())
                .supplierId(tradeOrderBO.getTradeCompanyId()).supplierName(companyFacade.queryOne(new CompanyQuery().setId(tradeOrderBO.getTradeCompanyId())).getCompanyName())
                .supplierBankId(Objects.isNull(companyBankBO) ? null : companyBankBO.getId()).signedDate(DateUtil.date())
                .createTime(DateUtil.date()).shipmentsDate(orderBO.getShipmentsTime()).build();
        billParam.setSettlementDate(getSettlementDate(orderBO, hangingOrderBO, billParam));

        //  设置付款主体
        if (Objects.nonNull(payerConfigBO)) {
            billParam.setPayCompanyId(payerConfigBO.getPayerId());
            PayerBO payerBO = payerFacade.getOne(new PayerQuery().setId(payerConfigBO.getPayerId()));
            if (Objects.nonNull(payerBO)) {
                billParam.setPayCompanyName(payerBO.getPayName());
            }
        }
        billFacade.save(billParam);
    }


    /**
     * 获取结算日期
     *
     * @param orderBO
     * @param hangingOrderBO
     * @return
     */
    private LocalDate getSettlementDate(OrderBO orderBO, HangingOrderBO hangingOrderBO, BillParam billParam) {
        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(hangingOrderBO.getLastCompeteCompany()));
        DateTime dateTime = null;
        if (Objects.isNull(companyBO) || Objects.isNull(companyBO.getAccountingPeriod())) {
            dateTime = DateUtil.offsetDay(orderBO.getSendTime(), hangingOrderBO.getAccountingPeriod() + 1);
        } else {
            billParam.setAccounting(companyBO.getAccountingPeriod());
            dateTime = DateUtil.offsetDay(orderBO.getSendTime(), companyBO.getAccountingPeriod() + 1);
        }
        if (dateTime.isBeforeOrEquals(DateUtil.date())) {
            return LocalDate.now().plusDays(1);
        }
        return dateTime.toLocalDateTime().toLocalDate();

    }


    public PageBO<TodayBillPayPlanVO> pageList(BillForm param, PageParamV2 pageParamV2) {
        List<Integer> codeList = List.of(BillPayPlanConsts.Status.WAIT_CONFIRM.getCode(), BillPayPlanConsts.Status.ERROR.getCode(), BillPayPlanConsts.Status.SYSTEM_CONFIRM.getCode(), BillPayPlanConsts.Status.USER_CONFIRM.getCode());
        ;
        BillPayPlanQuery billPayPlanQuery = new BillPayPlanQuery().setSupplierId(param.getSupplierId()).setStatusList(codeList);
        if (Objects.nonNull(param.getStartDate())) {
            billPayPlanQuery.setGtCreateTime(DateUtil.beginOfDay(param.getStartDate()));
        }
        if (Objects.nonNull(param.getEndDate())) {
            billPayPlanQuery.setLteCreateTime(DateUtil.endOfDay(param.getEndDate()));
        }
        PageBO<BillPayPlanBO> billPayPlanBOPageBO = billPayPlanFacade.listPage(billPayPlanQuery, pageParamV2);
        return new PageBO<>(BillConvert.INSTANCE.toDate(billPayPlanBOPageBO.getData()), billPayPlanBOPageBO.getTotal());
    }

    public void confirm(Long id, Long supplierId) {

        billPayPlanFacade.update(new BillPayPlanParam().setStatus(BillPayPlanConsts.Status.USER_CONFIRM.getCode()), new BillPayPlanQuery().setSupplierId(supplierId).setId(id).setStatus(BillPayPlanConsts.Status.WAIT_CONFIRM.getCode()));

    }

    public void fail(Long id, Long supplierId) {

        billPayPlanFacade.update(new BillPayPlanParam().setStatus(BillPayPlanConsts.Status.ERROR.getCode()), new BillPayPlanQuery().setSupplierId(supplierId).setId(id).setStatus(BillPayPlanConsts.Status.WAIT_CONFIRM.getCode()));

    }

    /**
     * 查询所有数据
     *
     * @param
     * @return
     */
    public List<BillExportVO> export(Long planId, Long supplierId) {

        List<BillBO> list = billFacade.list(new BillQuery().setSupplierId(supplierId).setPlanId(planId), null);

        return BillConvert.INSTANCE.toExcelVO(list);
    }
}
