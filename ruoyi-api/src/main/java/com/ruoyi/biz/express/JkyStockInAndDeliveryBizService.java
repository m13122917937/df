package com.ruoyi.biz.express;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.bill.constant.PayerConsts;
import com.ruoyi.bill.facade.IPayerFacade;
import com.ruoyi.bill.model.bo.PayerBO;
import com.ruoyi.bill.model.query.PayerQuery;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.param.JkyStockInAndDeliveryParam;
import com.ruoyi.jky.param.inspect.InspectParam;
import com.ruoyi.jky.param.logistics.LogisticsUpdateParam;
import com.ruoyi.jky.param.stock.StockCreateAndStockInParam;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.query.ImeiQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.product.facade.IProductSkuFacade;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.query.ProductSkuQuery;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.web.form.ExpressOrderForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JkyStockInAndDeliveryBizService {

    @Autowired
    private RuoYiConfig ruoYiConfig;

    @Autowired
    private JkyTemplate jkyTemplate;

    @Autowired
    private ITradeOrderFacade tradeOrderFacade;

    @Autowired
    private IImeiFacade iMeiFacade;

    @Autowired
    private IProductSkuFacade productSkuFacade;

    @Autowired
    private ICompanyFacade companyFacade;

    @Autowired
    private IPayerFacade payerFacade;

    /**
     * 创建吉客云验货、入库并回传物流信息。
     */
    public void createJkyStockIn(OrderBO orderBO, ExpressOrderForm expressOrderForm) {
        try {
            JkyStockInAndDeliveryParam param = builderJkyStockInAndDelivery(orderBO, expressOrderForm);
            jkyTemplate.inspectStockInAndDelivery(param);
        } catch (Exception e) {
            log.error("订单号：{}，吉客云入库发货失败：{}", orderBO.getOrderCode(), e.getMessage(), e);
        }
    }

    private JkyStockInAndDeliveryParam builderJkyStockInAndDelivery(OrderBO orderBO, ExpressOrderForm expressOrderForm) {
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        Assert.notNull(tradeOrderBO, "吉客云入库成交订单不存在");
        List<String> snList = queryJkySnList(orderBO);
        return new JkyStockInAndDeliveryParam()
                .setInspectParam(builderJkyInspect(orderBO, snList))
                .setStockInParam(builderJkyStockIn(orderBO, tradeOrderBO, orderBO.getQuantity(), snList))
                .setLogisticsUpdateParam(builderJkyLogisticsUpdate(orderBO, expressOrderForm));
    }

    private InspectParam builderJkyInspect(OrderBO orderBO, List<String> snList) {
        InspectParam.InspectDetail detail = new InspectParam.InspectDetail();
        detail.setGoodsNo(orderBO.getSkuCode());
        detail.setSkuName(StrUtil.blankToDefault(orderBO.getProductName(), orderBO.getSkuName()));
        detail.setSnList(JacksonUtil.toJson(snList));
        InspectParam param = new InspectParam();
        param.setNumber(StrUtil.blankToDefault(orderBO.getErpOrderId(), orderBO.getOrderCode()));
        param.setChecker("system");
        param.setInspectDetailList(Collections.singletonList(detail));
        param.setIsCheckOrder(0);
        param.setIsRefundDetect(0);
        return param;
    }

    private StockCreateAndStockInParam builderJkyStockIn(OrderBO orderBO, TradeOrderBO tradeOrderBO, Integer quantity, List<String> snList) {
        ProductSkuBO productSkuBO = productSkuFacade.getOne(new ProductSkuQuery().setSkuCode(orderBO.getSkuCode()));
        Assert.notNull(productSkuBO, "吉客云入库商品不存在");
        PayerBO payerBO = payerFacade.getOne(new PayerQuery().setId(orderBO.getPayerId()).setActived(PayerConsts.Activated.ACTIVATED.getCode()));
        Assert.notNull(payerBO, "吉客云入库付款主体不存在或已弃用");
        Assert.notBlank(payerBO.getOutCode(), "吉客云入库付款主体吉客云编号不能为空");
        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(tradeOrderBO.getTradeCompanyId()));
        Assert.notNull(companyBO, "吉客云入库供应商不存在");
        Assert.notBlank(companyBO.getOutNo(), "吉客云入库供应商编码不能为空");
        Assert.notBlank(ruoYiConfig.getWarehouseNo(), "吉客云入库仓库编码不能为空");
        Assert.notNull(quantity, "吉客云入库数量不能为空");
        Assert.isTrue(quantity > 0, "吉客云入库数量必须大于0");
        String remark = String.format("代发订单号:%s,供应商名称:%s", orderBO.getOrderCode(), tradeOrderBO.getTradeNickName());
        StockCreateAndStockInParam.StockInDetailView detail = new StockCreateAndStockInParam.StockInDetailView()
                .setSkuBarcode(productSkuBO.getSkuCode()).setRelDetailId(tradeOrderBO.getId()).setSkuCount(new BigDecimal(quantity))
                .setSkuPrice(tradeOrderBO.getTradePrice()).setIsCertified(1).setRowRemark(remark);
        if (CollectionUtil.isNotEmpty(snList)) {
            detail.setSerialList(snList.stream().map(sn -> new StockCreateAndStockInParam.Serial().setSerialNo(sn)).collect(Collectors.toList()));
        }
        return new StockCreateAndStockInParam().setVendCode(companyBO.getOutNo()).setApplyDepartCode(JkyTemplate.STOCK_IN_APPLY_DEPART_CODE).setApplyCompanyCode(payerBO.getOutCode())
                .setInWarehouseCode(ruoYiConfig.getWarehouseNo()).setRelDataId(orderBO.getOrderCode()).setApplyUserName("system")
                .setApplyDate(DateUtil.date()).setMemo(remark).setOperator("system")
                .setLogisticName(tradeOrderBO.getTrackingCompany()).setLogisticNo(tradeOrderBO.getTrackingNumber()).setStockInDetailViews(Collections.singletonList(detail));
    }

    private List<String> queryJkySnList(OrderBO orderBO) {
        List<ImeiBO> list = iMeiFacade.list(new ImeiQuery().setOrderId(orderBO.getOrderCode()));
        return list.stream().map(item -> Objects.equals("小米", orderBO.getBrand()) ? item.getImel() : item.getSn()).collect(Collectors.toList());
    }

    private LogisticsUpdateParam builderJkyLogisticsUpdate(OrderBO orderBO, ExpressOrderForm expressOrderForm) {
        LogisticsUpdateParam.LogisticsUpdateItem item = new LogisticsUpdateParam.LogisticsUpdateItem();
        item.setOrderNo(StrUtil.blankToDefault(orderBO.getErpOrderId(), orderBO.getOrderCode()));
        item.setLogisticNo(expressOrderForm.getTrackingNumber());
        item.setLogisticName(expressOrderForm.getTrackingCompany());
        item.setLogisticCode(expressOrderForm.getTrackingCompanyCode());
        LogisticsUpdateParam param = new LogisticsUpdateParam();
        param.setBizdata(Collections.singletonList(item));
        return param;
    }
}
