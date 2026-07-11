package com.ruoyi.biz.bill;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.bill.constant.BillConsts;
import com.ruoyi.bill.constant.BillPayPlanConsts;
import com.ruoyi.bill.facade.IBillFacade;
import com.ruoyi.bill.facade.IBillPayPlanFacade;
import com.ruoyi.bill.facade.IPayerFacade;
import com.ruoyi.bill.model.bo.*;
import com.ruoyi.bill.model.param.BillParam;
import com.ruoyi.bill.model.param.BillPayPlanParam;
import com.ruoyi.bill.model.query.BillPayPlanQuery;
import com.ruoyi.bill.model.query.BillQuery;
import com.ruoyi.bill.model.query.PayerQuery;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.exception.ServiceException;
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
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.user.facade.ICompanyBankFacade;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.bo.CompanyBankBO;
import com.ruoyi.user.model.consts.CompanyBankConsts;
import com.ruoyi.user.model.consts.CompanyEnum;
import com.ruoyi.user.model.query.CompanyBankQuery;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.web.form.bill.ConfirmSplitForm;
import com.ruoyi.web.form.bill.SplitForm;
import com.ruoyi.web.vo.bill.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BillBizService {

    @Autowired
    ICompanyFacade companyFacade;

    @Autowired
    IPayerFacade payerFacade;

    @Autowired
    IBillFacade billFacade;

    @Autowired
    IBillPayPlanFacade payPlanFacade;

    @Autowired
    ICompanyBankFacade companyBankFacade;

    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    /**
     * 查询今天待付款分页列表
     * 1、查询今天需要付款的账单汇总列表
     * 2、组装已经排款的资金
     *
     * @param payCompanyId 付款主体
     * @param supplierId
     * @param billType
     * @param pageParamV2
     * @return
     */
    public PageBO<BillSumVO> queryBillSUM(Long payCompanyId, Long supplierId, Integer billType, PageParamV2 pageParamV2) {

        BillQuery billQuery = new BillQuery().setSupplierId(supplierId).setPayCompanyId(payCompanyId)
                .setBillType(billType).setPayDate(DateUtil.date());
        PageBO<PaymentOperationsBO> paymentOperationsBOPageBO = billFacade.pageSum(billQuery, pageParamV2);
        if (CollUtil.isEmpty(paymentOperationsBOPageBO.getData())) {
            return new PageBO(CollUtil.newArrayList(), paymentOperationsBOPageBO.getTotal());
        }
        // 查询排款计划
        Set<Long> supplierIds = paymentOperationsBOPageBO.getData().stream().map(PaymentOperationsBO::getSupplierId).collect(Collectors.toSet());
        BillPayPlanQuery planQuery = new BillPayPlanQuery().setGtCreateTime(DateUtil.beginOfDay(DateUtil.date())).setSupplierIdSet(supplierIds);
        Map<Long, List<BillPayPlanBO>> billPlanMap = payPlanFacade.list(planQuery, null).stream().collect(Collectors.groupingBy(BillPayPlanBO::getSupplierId));
        List<BillSumVO> billSumVOS = BillConvert.INSTANCE.toVO(paymentOperationsBOPageBO.getData());
        Set<Long> supplierIdSet = billSumVOS.stream().map(BillSumVO::getSupplierId).collect(Collectors.toSet());
        List<CompanyBankBO> list = companyBankFacade.list(new CompanyBankQuery().setCompanyIdSet(supplierIdSet).setDefaulted(CompanyBankConsts.Defaulted.YES.getValue()), null);
        Map<Long, CompanyBankBO> companyBankBOMap = list.stream().collect(Collectors.toMap(CompanyBankBO::getCompanyId, e -> e));

        Map<Long, PayerBO> payerBOS = new HashMap<>();

        for (BillSumVO billSumVO : billSumVOS) {
            List<BillPlanVO> billPlanVOS = BillConvert.INSTANCE.billPayPlantoVOList(billPlanMap.get(billSumVO.getSupplierId()));
            if (CollUtil.isEmpty(billPlanVOS)){
                continue;
            }
            for (BillPlanVO billPlanVO : billPlanVOS) {
                PayerBO payerBO = queryPayCompany(payerBOS, billPlanVO.getPayCompanyId());
                billPlanVO.setPayCompanyBank(payerBO.getBankName());
                billPlanVO.setPayCompanyBankAccount(payerBO.getPayNo());
            }
            billSumVO.setBillPlanVOS(billPlanVOS);
            CompanyBankBO companyBankBO = companyBankBOMap.get(billSumVO.getSupplierId());
            if (Objects.nonNull(companyBankBO)) {
                billSumVO.setBankName(companyBankBO.getAccountBank());
                billSumVO.setBankAccount(companyBankBO.getBankAccount());
            }
        }
        return new PageBO<>(billSumVOS, paymentOperationsBOPageBO.getTotal());
    }

    private PayerBO queryPayCompany(Map<Long, PayerBO> payerBOS, Long payCompanyId) {
        PayerBO payerBO = payerBOS.getOrDefault(payCompanyId, payerFacade.getOne(new PayerQuery().setId(payCompanyId)));
        payerBOS.put(payCompanyId, payerBO);
        return payerBO;
    }

    @Transactional
    public void generateBill(OrderBO orderBO) {
        if (Objects.isNull(orderBO)) {
            return;
        }
        // 已有账单则跳过（防止重复出账）
        BillBO existBill = billFacade.getOne(new BillQuery().setOrderCode(orderBO.getOrderCode()));
        if (Objects.nonNull(existBill)) {
            log.info("订单{}已有账单，跳过生成", orderBO.getOrderCode());
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
        CompanyBankBO companyBankBO = companyBankFacade.getOne(new CompanyBankQuery().setDefaulted(CompanyBankConsts.Defaulted.YES.getValue()).setCompanyId(tradeOrderBO.getTradeCompanyId()));

        // 计算结算时间
        BillParam billParam = BillParam.builder().orderCode(orderBO.getOrderCode()).originalOrderId(orderBO.getOriginalOrderId()).billType(orderBO.getOrderType()).reversed(BillConsts.BillReversedType.FORWARD_DIRECTION.getCode())
                .brand(orderBO.getBrand()).category(orderBO.getCategory()).productName(orderBO.getProductName()).skuName(orderBO.getSkuName()).skuCode(orderBO.getSkuCode())
                .quantity(tradeOrderBO.getQuantity()).tradePrice(tradeOrderBO.getTradePrice()).billingAmount(Arith.mul(tradeOrderBO.getTradePrice(), new BigDecimal(tradeOrderBO.getQuantity())))
                .payPlan(BillPayPlanConsts.PayPlan.NOT_PAYMENT.getCode()).status(BillConsts.BillPayStatus.NO_PAY_STATUS.getCode()).accounting(hangingOrderBO.getAccountingPeriod())
                .supplierId(tradeOrderBO.getTradeCompanyId()).supplierName(companyFacade.queryOne(new CompanyQuery().setId(tradeOrderBO.getTradeCompanyId())).getCompanyName())
                .supplierBankId(Objects.isNull(companyBankBO) ? null : companyBankBO.getId()).signedDate(DateUtil.date())
                .createTime(DateUtil.date()).shipmentsDate(orderBO.getShipmentsTime()).build();
        billParam.setSettlementDate(getSettlementDate(orderBO, hangingOrderBO, billParam));


        //  设置付款主体
        billParam.setPayCompanyId(orderBO.getPayerId());
        PayerBO payerBO = payerFacade.getOne(new PayerQuery().setId(orderBO.getPayerId()));
        if (Objects.nonNull(payerBO)) {
            billParam.setPayCompanyName(payerBO.getPayName());
        }
        billFacade.save(billParam);
    }

    /**
     * 获取结算日期
     * <p>入仓取挂单账期，代发优先取供应商账期（无则仍取挂单账期）。</p>
     * <p>结算日期 = 发货时间 + 账期 + 1 天，若早于今天则置为明天。</p>
     */
    private LocalDate getSettlementDate(OrderBO orderBO, HangingOrderBO hangingOrderBO, BillParam billParam) {
        Integer accountingPeriod = hangingOrderBO.getAccountingPeriod();
        // 代发订单优先取供应商账期
        if (Objects.equals(OrderConsts.OrderType.O2O.getCode(), orderBO.getOrderType())) {
            CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(hangingOrderBO.getLastCompeteCompany()));
            if (Objects.nonNull(companyBO) && Objects.nonNull(companyBO.getAccountingPeriod())) {
                accountingPeriod = companyBO.getAccountingPeriod();
                billParam.setAccounting(accountingPeriod);
            }
        }
        DateTime dateTime = DateUtil.offsetDay(orderBO.getSendTime(), accountingPeriod + 1);
        if (dateTime.isBeforeOrEquals(DateUtil.date())) {
            return LocalDate.now().plusDays(1);
        }
        return dateTime.toLocalDateTime().toLocalDate();
    }

    public List<BillSumVO> queryBillSUMExport(Long payCompanyId, Long supplierId, Integer billType) {
        BillQuery billQuery = new BillQuery().setSupplierId(supplierId).setPayCompanyId(payCompanyId)
                .setBillType(billType).setPayDate(DateUtil.date());
        PageBO<PaymentOperationsBO> paymentOperationsBOPageBO = billFacade.pageSum(billQuery, null);
        if (CollUtil.isEmpty(paymentOperationsBOPageBO.getData())) {
            return Collections.EMPTY_LIST;
        }
        // 查询排款计划
        Set<Long> supplierIds = paymentOperationsBOPageBO.getData().stream().map(PaymentOperationsBO::getSupplierId).collect(Collectors.toSet());
        BillPayPlanQuery planQuery = new BillPayPlanQuery().setGtCreateTime(DateUtil.beginOfDay(DateUtil.date())).setSupplierIdSet(supplierIds);
        Map<Long, List<BillPayPlanBO>> billPlanMap = payPlanFacade.list(planQuery, null).stream().collect(Collectors.groupingBy(BillPayPlanBO::getSupplierId));
        List<BillSumVO> billSumVOS = BillConvert.INSTANCE.toVO(paymentOperationsBOPageBO.getData());
        Set<Long> supplierIdSet = billSumVOS.stream().map(BillSumVO::getSupplierId).collect(Collectors.toSet());
        List<CompanyBankBO> list = companyBankFacade.list(new CompanyBankQuery().setCompanyIdSet(supplierIdSet).setDefaulted(CompanyBankConsts.Defaulted.YES.getValue()), null);
        Map<Long, CompanyBankBO> companyBankBOMap = list.stream().collect(Collectors.toMap(CompanyBankBO::getCompanyId, e -> e));


        for (BillSumVO billSumVO : billSumVOS) {
            billSumVO.setBillPlanVOS(BillConvert.INSTANCE.billPayPlantoVOList(billPlanMap.get(billSumVO.getSupplierId())));
            CompanyBankBO companyBankBO = companyBankBOMap.get(billSumVO.getSupplierId());
            if (Objects.nonNull(companyBankBO)) {
                billSumVO.setBankName(companyBankBO.getAccountBank());
                billSumVO.setBankAccount(companyBankBO.getBankAccount());
            }
        }
        return billSumVOS;
    }


    public PageBO<BillVO> pageList(Long payCompanyId, Long supplierId, Integer billType, PageParamV2 pageParamV2) {

        BillQuery billQuery = new BillQuery().setSupplierId(supplierId).setPayCompanyId(payCompanyId)
                .setBillType(billType).setPayPlan(BillPayPlanConsts.PayPlan.NOT_PAYMENT.getCode()).setTodayShow(BillConsts.TodayShow.SHOW.getCode());

        PageBO<BillBO> billBOPageBO = billFacade.listPage(billQuery, pageParamV2);
        return new PageBO<>(BillConvert.INSTANCE.toBillVOList(billBOPageBO.getData()), billBOPageBO.getTotal());
    }

    public PaySummaryVO split(SplitForm splitForm) {

        Long billId = splitForm.getBillIds().iterator().next();
        BillBO billBO = billFacade.getOne(new BillQuery().setId(billId));
        // 判断企业是否被停款
        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(billBO.getSupplierId()).setStopPurchase(CompanyEnum.StopReasonEnum.USE.getValue()));
        if (Objects.isNull(companyBO)) {
            throw new ServiceException("供应商已经被停款，请先解除停款");
        }
        List<BillBO> list = billFacade.list(new BillQuery().setIdSet(splitForm.getBillIds()), null);
        List<PayCompanySummaryVO> payCompanySummaryVOS = list.stream().filter(Objects::nonNull)
                .collect(Collectors.groupingBy(bill -> Map.entry(bill.getPayCompanyId(), bill.getPayCompanyName()), Collectors.toList()
                )).entrySet().stream().map(entry -> {
                    List<BillBO> bills = entry.getValue();
                    BigDecimal totalAmount = bills.stream().map(bill -> Optional.ofNullable(bill.getBillingAmount()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
                    List<Long> ids = bills.stream().map(BillBO::getId).collect(Collectors.toList());
                    return new PayCompanySummaryVO(entry.getKey().getKey(), entry.getKey().getValue(), totalAmount, ids);
                }).collect(Collectors.toList());
        PaySummaryVO paySummaryVO = new PaySummaryVO();
        paySummaryVO.setSupplierName(list.get(0).getSupplierName());
        paySummaryVO.setSupplierId(list.get(0).getSupplierId());
        paySummaryVO.setPayCompanySummaryVOS(payCompanySummaryVOS);
        CompanyBankBO companyBankBO = companyBankFacade.getOne(new CompanyBankQuery().setCompanyId(list.get(0).getSupplierId()).setDefaulted(CompanyBankConsts.Defaulted.YES.getValue()));
        if (Objects.nonNull(companyBankBO)) {
            paySummaryVO.setBankName(companyBankBO.getAccountBank());
            paySummaryVO.setBankAccount(companyBankBO.getBankAccount());
        }
        return paySummaryVO;
    }

    /**
     *
     * @param splitForm
     */
    @Transactional
    public void plan(ConfirmSplitForm confirmSplitForm, LoginUser loginUser) {
        for (SplitForm splitForm : confirmSplitForm.getSplitForms()) {

            conf(splitForm, loginUser);
        }
    }

    void conf(SplitForm splitForm, LoginUser loginUser) {
        List<BillBO> list = billFacade.list(new BillQuery().setIdSet(splitForm.getBillIds()), null);
        List<PayCompanySummaryVO> payCompanySummaryVOS = list.stream().filter(Objects::nonNull) // 防止 list 中有 null 元素
                .collect(Collectors.groupingBy(bill -> Map.entry(bill.getPayCompanyId(), bill.getPayCompanyName()),
                        Collectors.reducing(BigDecimal.ZERO, bill -> Optional.ofNullable(bill.getBillingAmount()).orElse(BigDecimal.ZERO), BigDecimal::add)
                )).entrySet().stream().map(e -> new PayCompanySummaryVO(e.getKey().getKey(), e.getKey().getValue(), e.getValue(), null))
                .collect(Collectors.toList());

        for (PayCompanySummaryVO payCompanySummaryVO : payCompanySummaryVOS) {
            List<Long> billIdList = list.stream().filter(bill -> Objects.equals(bill.getPayCompanyId(), payCompanySummaryVO.getPayCompanyId())).map(BillBO::getId).collect(Collectors.toList());
            // 设置供应商银行卡id
            Long supplierBankId = Objects.isNull(splitForm.getSupplierBankId()) ? list.get(0).getSupplierBankId() : splitForm.getSupplierBankId();

            CompanyBankBO companyBankBO = companyBankFacade.getOne(new CompanyBankQuery().setId(supplierBankId));
            // 设置付款账号
            PayerQuery payerQuery = Objects.isNull(splitForm.getPayerId()) ? new PayerQuery().setId(payCompanySummaryVO.getPayCompanyId()) : new PayerQuery().setId(splitForm.getPayerId());
            Long payerId = Objects.isNull(splitForm.getPayerId()) ? payCompanySummaryVO.getPayCompanyId() : splitForm.getPayerId();
            PayerBO payerBO = payerFacade.getOne(payerQuery);
            // 保存计划
            BillPayPlanParam payPlanParam = new BillPayPlanParam().setSupplierId(list.get(0).getSupplierId()).setPayAmount(payCompanySummaryVO.getTotalBillingAmount())
                    .setPayCompanyId(payerId).setPayCompanyName(payCompanySummaryVO.getPayCompanyName()).setBillType(list.get(0).getBillType())
                    .setSupplierBankId(supplierBankId).setStatus(BillPayPlanConsts.Status.WAIT_PAY.getCode()).setCreateBy(loginUser.getUserId()).setCreateTime(DateUtil.date())
                    .setPayAccount(payerBO.getPayNo()).setSupplierName(companyBankBO.getCompanyName());
            BillPayPlanBO billPayPlanBO = payPlanFacade.save(payPlanParam);
            // 修改账单
            billFacade.update(BillParam.builder().payPlan(BillPayPlanConsts.PayPlan.PAYMENT.getCode()).planId(billPayPlanBO.getId()).build(), new BillQuery().setIdSet(new HashSet<>(billIdList)));
        }
    }


    public SumBillVO querySum(Long payCompanyId, Long supplierId, Integer billType) {

        BillQuery billQuery = new BillQuery().setSupplierId(supplierId).setPayCompanyId(payCompanyId)
                .setBillType(billType).setPayDate(DateUtil.date());
        BillSUMBO sum = billFacade.sum(billQuery);
        return BillConvert.INSTANCE.toSum(sum);
    }

    /**
     * 撤销排款
     *
     * @param planId
     * @param loginUser
     */
    @Transactional
    public void revoke(Long planId, LoginUser loginUser) {
        log.info("撤销排款：{},撤销人：{}", planId, loginUser.getUserId());
        //更新 bill 标
        billFacade.update(BillParam.builder().planId(null).payPlan(BillPayPlanConsts.PayPlan.NOT_PAYMENT.getCode()).build(), new BillQuery().setPlanId(planId));
        // 更新
        boolean delete = payPlanFacade.delete(new BillPayPlanQuery().setId(planId));
    }


    public List<BillVO> queryBillExport(Long payCompanyId, Long supplierId, Integer billType) {

        BillQuery billQuery = new BillQuery().setSupplierId(supplierId).setPayCompanyId(payCompanyId)
                .setBillType(billType).setTodayShow(BillConsts.TodayShow.SHOW.getCode());

        List<BillBO> billBOPageBO = billFacade.list(billQuery, null);
        return BillConvert.INSTANCE.toBillVOList(billBOPageBO);
    }
}
