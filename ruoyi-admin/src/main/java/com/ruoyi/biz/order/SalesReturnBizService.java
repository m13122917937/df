package com.ruoyi.biz.order;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.bill.constant.BillConsts;
import com.ruoyi.bill.constant.BillPayPlanConsts;
import com.ruoyi.bill.facade.IBillFacade;
import com.ruoyi.bill.facade.IPayerConfigFacade;
import com.ruoyi.bill.facade.IPayerFacade;
import com.ruoyi.bill.model.bo.PayerBO;
import com.ruoyi.bill.model.bo.PayerConfigBO;
import com.ruoyi.bill.model.param.BillParam;
import com.ruoyi.bill.model.query.PayerConfigQuery;
import com.ruoyi.bill.model.query.PayerQuery;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.mapper.salesReturn.SalesReturnConvert;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ISalesReturnFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.SalesReturnBO;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.SalesReturnParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.SalesReturnQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.user.facade.ICompanyBankFacade;
import com.ruoyi.user.model.bo.CompanyBankBO;
import com.ruoyi.user.model.consts.CompanyBankConsts;
import com.ruoyi.user.model.query.CompanyBankQuery;
import com.ruoyi.web.form.order.SalesReturnListForm;
import com.ruoyi.web.form.order.SalesReturnSaveForm;
import com.ruoyi.web.vo.order.SalesReturnOrderDetailVO;
import com.ruoyi.web.vo.order.SalesReturnVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 销售退货业务逻辑
 *
 * @author ruoyi
 * @date 2026-06-30
 */
@Slf4j
@Component
public class SalesReturnBizService {

    @Autowired
    private ISalesReturnFacade salesReturnFacade;

    @Autowired
    private SalesReturnNoGenerator salesReturnNoGenerator;

    @Autowired
    private IOrderFacade orderFacade;

    @Autowired
    private ITradeOrderFacade tradeOrderFacade;

    @Autowired
    private IBillFacade billFacade;

    @Autowired
    private ICompanyBankFacade companyBankFacade;

    @Autowired
    private IPayerConfigFacade payerConfigFacade;

    @Autowired
    private IPayerFacade payerFacade;

    /**
     * 分页查询销售退货列表
     */
    public PageBO<SalesReturnVO> listPage(SalesReturnListForm listForm, PageParamV2 pageParam) {
        SalesReturnQuery query = SalesReturnConvert.INSTANCE.toQuery(listForm);
        PageBO<SalesReturnBO> pageBO = salesReturnFacade.listPage(query, pageParam);
        PageBO<SalesReturnVO> result = new PageBO<>();
        result.setTotal(pageBO.getTotal());
        result.setData(SalesReturnConvert.INSTANCE.toVOList(pageBO.getData()));
        return result;
    }

    /**
     * 新增销售退货单
     */
    @Transactional
    public void save(SalesReturnSaveForm form, LoginUser loginUser) {
        SalesReturnParam param = SalesReturnConvert.INSTANCE.toParam(form);
        // 生成退货单号
        String returnCode = salesReturnNoGenerator.next();
        param.setReturnCode(returnCode);
        // 计算总金额
        BigDecimal totalAmount = form.getReturnPrice().multiply(BigDecimal.valueOf(form.getQuantity()));
        param.setTotalAmount(totalAmount);
        // 默认状态：已创建
        param.setStatus(0);
        param.setCreateTime(DateUtil.date());
        param.setCreateBy(String.valueOf(loginUser.getUserId()));

        // 保存退货单
        salesReturnFacade.save(param);

        // 生成财务账单（应付扣款）
        createBillRecord(param, loginUser);
    }

    /**
     * 根据内部单号或商家单号查询原订单详情（供前端自动带出）
     */
    public SalesReturnOrderDetailVO getOrderDetail(String orderCode, String originalOrderId) {
        OrderBO orderBO = null;
        if (StrUtil.isNotBlank(orderCode)) {
            orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        } else if (StrUtil.isNotBlank(originalOrderId)) {
            orderBO = orderFacade.getOne(new OrderQuery().setOriginalOrderId(originalOrderId));
        }
        if (orderBO == null) {
            return null;
        }

        SalesReturnOrderDetailVO vo = SalesReturnConvert.INSTANCE.toOrderDetailVO(orderBO);

        // 查询供应商信息
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(
                new TradeOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        if (tradeOrderBO != null) {
            vo.setTradePrice(tradeOrderBO.getTradePrice());
            vo.setCompanyId(tradeOrderBO.getTradeCompanyId());
            vo.setCompanyName(tradeOrderBO.getTradeNickName());
        }

        return vo;
    }

    /**
     * 创建财务账单记录
     */
    private void createBillRecord(SalesReturnParam param, LoginUser loginUser) {
        // 查询供应商银行信息
        CompanyBankBO companyBankBO = null;
        if (param.getCompanyId() != null) {
            companyBankBO = companyBankFacade.getOne(
                    new CompanyBankQuery().setDefaulted(CompanyBankConsts.Defaulted.YES.getValue())
                            .setCompanyId(param.getCompanyId()));
        }

        BillParam billParam = BillParam.builder()
                .orderCode(param.getReturnCode())
                .originalOrderId(param.getOriginalOrderId())
                .billType(BillConsts.BillType.BATCH_PURCHASE.getCode())
                .reversed(BillConsts.BillReversedType.REVERSE.getCode())
                .brand(param.getBrand())
                .category(param.getCategory())
                .productName(param.getProductName())
                .skuName(param.getSkuName())
                .skuCode(param.getSkuCode())
                .quantity(param.getQuantity())
                .tradePrice(param.getReturnPrice())
                .billingAmount(param.getTotalAmount())
                .payPlan(BillPayPlanConsts.PayPlan.NOT_PAYMENT.getCode())
                .status(BillConsts.BillPayStatus.NO_PAY_STATUS.getCode())
                .accounting(0)
                .supplierId(param.getCompanyId())
                .supplierName(param.getCompanyName())
                .supplierBankId(companyBankBO != null ? companyBankBO.getId() : null)
                .signedDate(DateUtil.date())
                .createTime(DateUtil.date())
                .shipmentsDate(null)
                .settlementDate(DateUtil.offsetDay(DateUtil.date(), 1).toLocalDateTime().toLocalDate())
                .build();

        // 设置付款主体
        PayerConfigBO payerConfigBO = payerConfigFacade.getOne(new PayerConfigQuery().setKeyWord(""));
        if (payerConfigBO == null) {
            payerConfigBO = payerConfigFacade.listPage(new PayerConfigQuery(), new PageParamV2()).getData().get(0);
        }
        if (payerConfigBO != null) {
            billParam.setPayCompanyId(payerConfigBO.getPayerId());
            PayerBO payerBO = payerFacade.getOne(new PayerQuery().setId(payerConfigBO.getPayerId()));
            if (payerBO != null) {
                billParam.setPayCompanyName(payerBO.getPayName());
            }
        }

        billFacade.save(billParam);

        // 更新退货单状态为已扣款
        SalesReturnParam updateParam = new SalesReturnParam().setStatus(1).setUpdateTime(DateUtil.date());
        SalesReturnQuery updateQuery = new SalesReturnQuery().setReturnCode(param.getReturnCode());
        salesReturnFacade.update(updateParam, updateQuery);
    }

    /**
     * 查询退货单详情
     */
    public SalesReturnVO getById(Long id) {
        SalesReturnBO bo = salesReturnFacade.getOne(new SalesReturnQuery().setId(id));
        if (bo == null) {
            return null;
        }
        return SalesReturnConvert.INSTANCE.toVO(bo);
    }
}
