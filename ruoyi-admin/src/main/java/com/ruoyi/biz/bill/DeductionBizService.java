package com.ruoyi.biz.bill;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.ruoyi.bill.constant.BillConsts;
import com.ruoyi.bill.constant.BillPayPlanConsts;
import com.ruoyi.bill.constant.DeductionConsts;
import com.ruoyi.bill.facade.IBillFacade;
import com.ruoyi.bill.facade.IDeductionFacade;
import com.ruoyi.bill.facade.IPayerConfigFacade;
import com.ruoyi.bill.facade.IPayerFacade;
import com.ruoyi.bill.model.bo.BillBO;
import com.ruoyi.bill.model.bo.DeductionBO;
import com.ruoyi.bill.model.bo.PayerBO;
import com.ruoyi.bill.model.bo.PayerConfigBO;
import com.ruoyi.bill.model.param.BillParam;
import com.ruoyi.bill.model.param.DeductionParam;
import com.ruoyi.bill.model.query.BillQuery;
import com.ruoyi.bill.model.query.DeductionQuery;
import com.ruoyi.bill.model.query.PayerConfigQuery;
import com.ruoyi.bill.model.query.PayerQuery;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.mapper.bill.BillConvert;
import com.ruoyi.mapper.bill.DeductionConvert;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.query.ImeiQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.user.facade.ICompanyBankFacade;
import com.ruoyi.user.model.bo.CompanyBankBO;
import com.ruoyi.user.model.consts.CompanyBankConsts;
import com.ruoyi.user.model.query.CompanyBankQuery;
import com.ruoyi.web.form.bill.DeductionSaveForm;
import com.ruoyi.web.vo.bill.DeductionInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DeductionBizService {

    @Autowired
    IDeductionFacade deductionFacade;


    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    IImeiFacade imeiFacade;

    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    @Autowired
    IBillFacade billFacade;

    @Autowired
    ICompanyBankFacade companyBankFacade;

    @Autowired
    IPayerConfigFacade payerConfigFacade;

    @Autowired
    IPayerFacade payerFacade;

    public PageBO<DeductionBO> listPage(DeductionQuery deductionQuery, PageParamV2 pageParamV2) {

        return deductionFacade.listPage(deductionQuery, pageParamV2);
    }

    /**
     * 添加扣罚记录
     *
     * @param deductionSaveForm
     * @param loginUser
     */
    @Transactional
    public void save(DeductionSaveForm deductionSaveForm, LoginUser loginUser) {
        DeductionParam deductionParam = DeductionConvert.INSTANCE.toParam(deductionSaveForm);
        deductionParam.setAmount(deductionSaveForm.getAmount().negate());

        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(deductionSaveForm.getOrderCode()));
        Assert.notNull(orderBO, "订单不存在");
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        Assert.notNull(tradeOrderBO, "抢单记录不存在");
        // 添加扣罚记录
        deductionParam.setTradePrice(tradeOrderBO.getTradePrice()).setCompanyId(tradeOrderBO.getTradeCompanyId()).setCompanyName(tradeOrderBO.getTradeNickName())
                .setErpId(orderBO.getErpOrderId()).setOriginalOrderId(orderBO.getOriginalOrderId()).setBrand(orderBO.getBrand()).setCategory(orderBO.getCategory())
                .setSendTime(orderBO.getSendTime()).setCreateTime(DateUtil.date()).setCreateBy(loginUser.getUserId()).setSpuName(orderBO.getProductName())
                .setSkuName(orderBO.getSkuName());
        deductionFacade.save(deductionParam);


        // 添加财务扣罚记录
        CompanyBankBO companyBankBO = companyBankFacade.getOne(new CompanyBankQuery().setDefaulted(CompanyBankConsts.Defaulted.YES.getValue()).setCompanyId(tradeOrderBO.getTradeCompanyId()));

        BillParam billParam = BillParam.builder().orderCode(orderBO.getOrderCode()).originalOrderId(orderBO.getOriginalOrderId()).billType(BillConsts.BillType.ONE_ITEM_SEND.getCode())
                .reversed(BillConsts.BillReversedType.ORDER_ABATE.getCode()).brand(orderBO.getBrand()).category(orderBO.getCategory()).productName(orderBO.getProductName()).skuName(orderBO.getSkuName())
                .skuCode(orderBO.getSkuCode()).quantity(orderBO.getQuantity()).tradePrice(new BigDecimal(200)).billingAmount(new BigDecimal(200))
                .payPlan(BillPayPlanConsts.PayPlan.NOT_PAYMENT.getCode()).status(BillConsts.BillPayStatus.NO_PAY_STATUS.getCode()).accounting(0)
                .supplierId(tradeOrderBO.getTradeCompanyId()).supplierName(tradeOrderBO.getTradeNickName()).supplierBankId(companyBankBO != null ? companyBankBO.getId() : null).signedDate(DateUtil.date())
                .createTime(DateUtil.date()).shipmentsDate(orderBO.getShipmentsTime()).settlementDate(DateUtil.offsetDay(DateUtil.date(), 1).toLocalDateTime().toLocalDate()).build();
        PayerConfigBO payerConfigBO = payerConfigFacade.getOne(new PayerConfigQuery().setKeyWord(orderBO.getShopName()));
        if (Objects.isNull(payerConfigBO)) {
            payerConfigBO = payerConfigFacade.listPage(new PayerConfigQuery(), new PageParamV2()).getData().get(0);
        }
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

    public DeductionInfoVO info(String orderCode) {


        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        Assert.notNull(orderBO, "订单不存在");
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        Assert.notNull(tradeOrderBO, "抢单记录不存在");
        DeductionInfoVO deductionInfoVO = new DeductionInfoVO();
        deductionInfoVO.setProductName(orderBO.getProductName()).setSkuName(orderBO.getSkuName());
        deductionInfoVO.setShopName(orderBO.getShopName()).setCompanyName(tradeOrderBO.getTradeNickName());
        deductionInfoVO.setSendTime(orderBO.getSendTime());
        // 查询串码信息
        List<ImeiBO> imeiBOS = imeiFacade.list(new ImeiQuery().setOrderId(orderCode));
        if (CollectionUtil.isNotEmpty(imeiBOS)) {
            deductionInfoVO.setSn(imeiBOS.stream().map(ImeiBO::getSn).filter(Objects::nonNull).collect(Collectors.joining(",")));
            deductionInfoVO.setImei(imeiBOS.stream().map(ImeiBO::getImel).filter(Objects::nonNull).collect(Collectors.joining(",")));
        }
        return deductionInfoVO;
    }

    public void revoke(Long id) {

        DeductionBO deductionBO = deductionFacade.getOne(new DeductionQuery().setId(id));
        Assert.notNull(deductionBO, "扣罚记录不存在");
        Assert.isTrue(Objects.equals(deductionBO.getStatus(), DeductionConsts.Status.DEDUCTION.getCode()), "扣罚已经撤销");

        deductionFacade.update(new DeductionParam().setStatus(DeductionConsts.Status.REVOKE.getCode()), new DeductionQuery().setId(id));

        BillBO billBO = billFacade.getOne(new BillQuery().setOrderCode(deductionBO.getOrderCode()).setBillType(BillConsts.BillType.ONE_ITEM_SEND.getCode()));
        if (Objects.nonNull(billBO)) {
            BillParam billParam = BillConvert.INSTANCE.toBillParam(billBO);
            billParam.setPayPlan(BillPayPlanConsts.PayPlan.NOT_PAYMENT.getCode());
            billParam.setSettlementDate(DateUtil.offsetDay(DateUtil.date(), 1).toLocalDateTime().toLocalDate());
            billParam.setStatus(BillConsts.BillPayStatus.NO_PAY_STATUS.getCode());
            billParam.setCreateTime(DateUtil.date());
            billParam.setBillingAmount(billParam.getBillingAmount().negate());
            billFacade.save(billParam);
        }
    }

}
