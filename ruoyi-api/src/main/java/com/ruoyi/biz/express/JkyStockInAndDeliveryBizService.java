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
import com.ruoyi.express.facade.impl.RouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.param.inspect.InspectParam;
import com.ruoyi.jky.param.stock.StockCreateAndStockInParam;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IJkyLogisticsTaskFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.JkyLogisticsTaskParam;
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
    private RouteSubscribeFacade routeSubscribeFacade;

    @Autowired
    private IJkyLogisticsTaskFacade jkyLogisticsTaskFacade;

    /**
     * 创建吉客云验货、入库、更新物流、上报序列号并直接发货。
     *
     * @param orderBO 订单
     */
    public void createJkyStockIn(OrderBO orderBO) {
        createJkyStockIn(orderBO,
                routeSubscribeFacade.getOne(new RouteSubscribeQuery().setOrderCode(orderBO.getOrderCode())));
    }

    /**
     * 创建吉客云验货、入库、更新物流、上报序列号并直接发货。
     *
     * @param orderBO          订单
     * @param routeSubscribeBO 物流订阅信息（为 null 时跳过物流更新）
     */
    public void createJkyStockIn(OrderBO orderBO, RouteSubscribeBO routeSubscribeBO) {
        List<ImeiBO> imeiList = queryJkyImeiList(orderBO);
        if (CollectionUtil.isEmpty(imeiList)) {
            log.warn("串码不存在，请你核查{}", orderBO.getOrderCode());
            return;
        }
        if (Objects.isNull(routeSubscribeBO)) {
            log.warn("物流单号不存在，请你核查{}", orderBO.getOrderCode());
            return;
        }
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery()
                .setOrderId(orderBO.getOrderCode())
                .setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        Assert.notNull(tradeOrderBO, "吉客云入库成交订单不存在");

        // 1. 创建库存并入库
        StockCreateAndStockInParam stockInParam = builderJkyStockIn(orderBO, tradeOrderBO, orderBO.getQuantity(), imeiList);
        jkyTemplate.createAndStockIn(stockInParam);
        // 2. 验货
        InspectParam inspectParam = builderJkyInspect(orderBO, imeiList);
        jkyTemplate.inspect(inspectParam);

        // 3. 插入延迟任务，5分钟后更新物流信息
        JkyLogisticsTaskParam param = builderJkyLogisticsTaskParam(orderBO, routeSubscribeBO);
        jkyLogisticsTaskFacade.save(param);
        log.info("订单号：{}，已插入吉客云物流更新延迟任务", orderBO.getOrderCode());

    }

    private InspectParam builderJkyInspect(OrderBO orderBO, List<ImeiBO> imeiList) {
        InspectParam.InspectDetail detail = new InspectParam.InspectDetail();
        detail.setSkuBarcode(orderBO.getSkuCode());
        if (CollectionUtil.isNotEmpty(imeiList)) {
            detail.setSnList(imeiList.get(0).getSn());
        }
        InspectParam param = new InspectParam();
        param.setNumber(orderBO.getErpOrderId());
        param.setInspectDetailList(Collections.singletonList(detail));
        param.setIsCheckOrder(0);
        param.setIsRefundDetect(0);
        return param;
    }

    private JkyLogisticsTaskParam builderJkyLogisticsTaskParam(OrderBO orderBO, RouteSubscribeBO routeSubscribeBO) {
        return new JkyLogisticsTaskParam()
                .setOrderCode(orderBO.getOrderCode())
                .setErpOrderId(orderBO.getErpOrderId())
                .setLogisticsNo(routeSubscribeBO.getLogisticsNo())
                .setLogisticsName(resolveJkyLogisticsName(DictUtils.getDictLabel(DictDataConsts.P_EXPRESS_COMPANY, routeSubscribeBO.getLogisticsCode())))
                .setStatus(0)
                .setExecuteTime(DateUtil.offsetMinute(DateUtil.date(), 5))
                .setCreateTime(DateUtil.date());
    }

    private StockCreateAndStockInParam builderJkyStockIn(OrderBO orderBO, TradeOrderBO tradeOrderBO, Integer quantity, List<ImeiBO> imeiList) {
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
        if (CollectionUtil.isNotEmpty(imeiList)) {
            detail.setSerialList(imeiList.stream().map(imei ->
                    new StockCreateAndStockInParam.Serial().setSerialNo(imei.getSn()).setSerialNo2(imei.getImel())
            ).collect(Collectors.toList()));
        }
        return new StockCreateAndStockInParam().setVendCode(companyBO.getOutNo()).setApplyDepartCode(JkyTemplate.STOCK_IN_APPLY_DEPART_CODE).setApplyCompanyCode(payerBO.getOutCode())
                .setInWarehouseCode(ruoYiConfig.getWarehouseNo()).setRelDataId(orderBO.getOrderCode()).setApplyUserName("system")
                .setApplyDate(DateUtil.date()).setMemo(remark).setOperator("system")
                .setLogisticName(tradeOrderBO.getTrackingCompany()).setLogisticNo(tradeOrderBO.getTrackingNumber()).setStockInDetailViews(Collections.singletonList(detail));
    }

    private List<ImeiBO> queryJkyImeiList(OrderBO orderBO) {
        return iMeiFacade.list(new ImeiQuery().setOrderId(orderBO.getOrderCode()));
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
