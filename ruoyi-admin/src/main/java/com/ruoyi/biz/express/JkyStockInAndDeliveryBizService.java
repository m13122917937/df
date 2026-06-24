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
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.param.JkyStockInAndDeliveryParam;
import com.ruoyi.jky.param.delivery.SendDirectParam;
import com.ruoyi.jky.param.logistics.LogisticsUpdateParam;
import com.ruoyi.web.form.jky.JkyCallbackForm;
import com.ruoyi.jky.param.inspect.InspectParam;
import com.ruoyi.jky.param.sn.SnReportParam;
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
import com.ruoyi.system.model.consts.DictDataConsts;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.query.CompanyQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private IRouteSubscribeFacade routeSubscribeFacade;

    /**
     * 创建吉客云验货、入库、更新物流、上报序列号并直接发货。
     *
     * @param orderBO          订单
     * @param routeSubscribeBO 物流订阅信息（为 null 时跳过物流更新）
     */
    public void createJkyStockIn(OrderBO orderBO, RouteSubscribeBO routeSubscribeBO) {
        List<String> snList = queryJkySnList(orderBO);
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery()
                .setOrderId(orderBO.getOrderCode())
                .setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        Assert.notNull(tradeOrderBO, "吉客云入库成交订单不存在");


        // 1. 创建库存并入库
        StockCreateAndStockInParam stockInParam = builderJkyStockIn(orderBO, tradeOrderBO, orderBO.getQuantity(), snList);
        jkyTemplate.createAndStockIn(stockInParam);
        // 2. 验货
        InspectParam inspectParam = builderJkyInspect(orderBO, snList);
        jkyTemplate.inspect(inspectParam);

        // 3. 更新物流信息
        if (Objects.nonNull(routeSubscribeBO)) {
            LogisticsUpdateParam logisticsUpdateParam = builderJkyLogisticsUpdate(orderBO, routeSubscribeBO);
            jkyTemplate.updateLogisticsInfo(logisticsUpdateParam);
        } else {
            log.info("订单号：{}，未查询到物流订阅信息，跳过吉客云更新物流", orderBO.getOrderCode());
        }
    }

    /**
     * 三方物流回调处理：解析 bizdata JSON，更新吉客云物流信息。
     */
    public void handleDeliveryCallback(final String bizdata) {
        if (StrUtil.isBlank(bizdata)) {
            log.warn("三方物流回调 bizdata 为空");
            return;
        }
        JkyCallbackForm form = parseBizdata(bizdata);
        if (form == null) {
            return;
        }
        LogisticsUpdateParam.LogisticsUpdateItem item = new LogisticsUpdateParam.LogisticsUpdateItem();
        item.setOrderNo(StrUtil.blankToDefault(form.getDeliveryOrderNo(), form.getOwnerOrderNo()));
        item.setLogisticNo(form.getLogisticNo());
        item.setLogisticName(resolveJkyLogisticsName(form.getLogisticName()));
        item.setLogisticCode(resolveJkyLogisticsCode(form.getLogisticCode()));

        LogisticsUpdateParam param = new LogisticsUpdateParam();
        param.setBizdata(Collections.singletonList(item));

        jkyTemplate.updateLogisticsInfo(param);
    }

    private static JkyCallbackForm parseBizdata(final String bizdata) {
        try {
            Map<String, Object> map = JacksonUtil.parse(bizdata, Map.class);
            JkyCallbackForm form = new JkyCallbackForm();
            form.setDeliveryOrderNo(StrUtil.toStringOrNull(map.get("deliveryorderno")));
            form.setOwnerOrderNo(StrUtil.toStringOrNull(map.get("ownerorderno")));
            form.setWarehouseCode(StrUtil.toStringOrNull(map.get("warehousecode")));
            form.setOwnerCode(StrUtil.toStringOrNull(map.get("ownercode")));
            form.setLogisticNo(StrUtil.toStringOrNull(map.get("logisticNo")));
            form.setLogisticName(StrUtil.toStringOrNull(map.get("logisticName")));
            form.setLogisticCode(StrUtil.toStringOrNull(map.get("logisticCode")));
            return form;
        } catch (Exception e) {
            log.error("bizdata 解析失败：{}", bizdata, e);
            return null;
        }
    }

    private JkyStockInAndDeliveryParam builderJkyStockInAndDelivery(OrderBO orderBO, LogisticsUpdateParam logisticsUpdateParam) {
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        Assert.notNull(tradeOrderBO, "吉客云入库成交订单不存在");
        List<String> snList = queryJkySnList(orderBO);
        return new JkyStockInAndDeliveryParam()
                .setInspectParam(builderJkyInspect(orderBO, snList))
                .setStockInParam(builderJkyStockIn(orderBO, tradeOrderBO, orderBO.getQuantity(), snList))
                .setLogisticsUpdateParam(logisticsUpdateParam);
    }

    private InspectParam builderJkyInspect(OrderBO orderBO, List<String> snList) {
        InspectParam.InspectDetail detail = new InspectParam.InspectDetail();
        detail.setSkuBarcode(orderBO.getSkuCode());
        detail.setSnList(snList.get(0));
        InspectParam param = new InspectParam();
        param.setNumber(StrUtil.blankToDefault(orderBO.getErpOrderId(), orderBO.getOrderCode()));
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

    private SnReportParam builderSnReport(OrderBO orderBO, List<String> snList) {
        ProductSkuBO productSkuBO = productSkuFacade.getOne(new ProductSkuQuery().setSkuCode(orderBO.getSkuCode()));
        Assert.notNull(productSkuBO, "吉客云序列号上报商品不存在");
        PayerBO payerBO = payerFacade.getOne(new PayerQuery().setId(orderBO.getPayerId()).setActived(PayerConsts.Activated.ACTIVATED.getCode()));
        Assert.notNull(payerBO, "吉客云序列号上报付款主体不存在");
        Assert.notBlank(payerBO.getOutCode(), "吉客云序列号上报付款主体吉客云编号不能为空");

        SnReportParam.SnReportGoods goods = new SnReportParam.SnReportGoods();
        goods.setOutSkuCode(productSkuBO.getSkuCode());
        goods.setName(StrUtil.blankToDefault(orderBO.getProductName(), orderBO.getSkuName()));
        goods.setBarcode(productSkuBO.getSkuCode());
        goods.setSnlist(snList);

        SnReportParam param = new SnReportParam();
        param.setDeliveryorderno(StrUtil.blankToDefault(orderBO.getErpOrderId(), orderBO.getOrderCode()));
        param.setOwnerorderno(orderBO.getOrderCode());
        param.setWarehousecode(ruoYiConfig.getWarehouseNo());
        param.setOwnercode(payerBO.getOutCode());
        param.setOutbizcode(orderBO.getOrderCode());
        param.setOperatorcode("system");
        param.setOperatorname("system");
        param.setOperatetime(DateUtil.now());
        param.setGoods(Collections.singletonList(goods));
        return param;
    }

    private List<String> queryJkySnList(OrderBO orderBO) {
        List<ImeiBO> list = iMeiFacade.list(new ImeiQuery().setOrderId(orderBO.getOrderCode()));
        return list.stream().map(item -> Objects.equals("小米", orderBO.getBrand()) ? item.getImel() : item.getSn()).collect(Collectors.toList());
    }

    private LogisticsUpdateParam builderJkyLogisticsUpdate(OrderBO orderBO, RouteSubscribeBO routeSubscribeBO) {
        LogisticsUpdateParam.LogisticsUpdateItem item = new LogisticsUpdateParam.LogisticsUpdateItem();
        item.setOrderNo(StrUtil.blankToDefault(orderBO.getErpOrderId(), orderBO.getOrderCode()));
        item.setLogisticNo(routeSubscribeBO.getLogisticsNo());
        item.setLogisticName(resolveJkyLogisticsName(DictUtils.getDictLabel(DictDataConsts.P_EXPRESS_COMPANY, routeSubscribeBO.getLogisticsCode())));
        item.setLogisticCode(resolveJkyLogisticsCode(routeSubscribeBO.getLogisticsCode()));
        LogisticsUpdateParam param = new LogisticsUpdateParam();
        param.setBizdata(Collections.singletonList(item));
        return param;
    }

    private static String resolveJkyLogisticsCode(final String logisticsCode) {
        if (StrUtil.isBlank(logisticsCode)) {
            return null;
        }
        switch (logisticsCode) {
            case "shunfeng":
                return "0001";
            default:
                return logisticsCode;
        }
    }

    private static String resolveJkyLogisticsName(final String logisticsName) {
        if (StrUtil.isBlank(logisticsName)) {
            return null;
        }
        switch (logisticsName) {
            case "顺丰快递":
                return "顺丰速运";
            default:
                return logisticsName;
        }
    }
}
