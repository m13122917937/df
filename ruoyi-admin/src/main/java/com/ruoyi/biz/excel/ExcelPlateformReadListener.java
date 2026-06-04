package com.ruoyi.biz.excel;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.param.JkyStockInAndDeliveryParam;
import com.ruoyi.jky.param.logistics.LogisticsUpdateParam;
import com.ruoyi.jky.param.sn.SnReportParam;
import com.ruoyi.jky.param.stock.StockCreateAndStockInParam;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.TradeOrderBO;
import com.ruoyi.order.model.consts.ImeiConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.ImeiQuery;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.TradeOrderQuery;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.system.model.consts.DictDataConsts;
import com.ruoyi.wangdian.param.stock.StockInInfoGoodsList;
import com.ruoyi.wangdian.param.stock.StockInInfoParam;
import com.ruoyi.wangdian.utils.WdtClient;
import com.ruoyi.web.vo.order.ExcelPlatformVO;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class ExcelPlateformReadListener implements ReadListener<ExcelPlatformVO> {

    IImeiFacade imeiFacade;

    IOrderFacade orderFacade;

    WdtClient wdtClient;

    JkyTemplate jkyTemplate;

    String warehouseNo;

    ITradeOrderFacade tradeOrderFacade;

    IRouteSubscribeFacade routeSubscribeFacade;


    public ExcelPlateformReadListener(IImeiFacade imeiFacade, IOrderFacade orderFacade, WdtClient client, JkyTemplate jkyTemplate, String warehouseNo, ITradeOrderFacade tradeOrderFacade, IRouteSubscribeFacade routeSubscribeFacade) {
        this.imeiFacade = imeiFacade;
        this.orderFacade = orderFacade;
        this.wdtClient = client;
        this.jkyTemplate = jkyTemplate;
        this.warehouseNo = warehouseNo;
        this.tradeOrderFacade = tradeOrderFacade;
        this.routeSubscribeFacade = routeSubscribeFacade;
    }

    @Override
    public void invoke(ExcelPlatformVO excelPlatformVO, AnalysisContext analysisContext) {
        log.info("excelPlatformVO:{}", excelPlatformVO);
        ImeiConsts.PlatformImei platformImei = ImeiConsts.PlatformImei.getByName(excelPlatformVO.getValidated());
        if (Objects.isNull(platformImei)) {
            return;
        }
        if (StrUtil.isBlank(excelPlatformVO.getOrderCode())) {
            return;
        }
        //
        boolean update = imeiFacade.update(new ImeiParam().setPlatformImei(platformImei.getCode()).setPlatformTime(DateUtil.date()), new ImeiQuery()
                .setOrderId(excelPlatformVO.getOrderCode()).setSn(excelPlatformVO.getSn()).setImel(excelPlatformVO.getImei()));
        if (!update) {
            return;
        }
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(excelPlatformVO.getOrderCode()));

        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(new RouteSubscribeQuery().setOrderCode(orderBO.getOrderCode()));

        // 修改订单状态
        if (Objects.equals(platformImei.getCode(), ImeiConsts.PlatformImei.NORMAL.getCode())) {
            if (Objects.nonNull(routeSubscribeBO)) {
                orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.DELIVERY_END.getCode()),
                        new OrderQuery().setOrderCode(excelPlatformVO.getOrderCode()));
                //创建入库单
                try {
                    wdtClient.stockInPush(builderStockIn(orderBO));
                    createJkyStockIn(orderBO, routeSubscribeBO, warehouseNo, orderBO.getQuantity());
                } catch (IOException e) {
                    log.error("订单号：{}，创建入库单失败：{}", orderBO.getOrderCode(), e.getMessage());
                }
            } else {
                log.info("待填写物流单号：{}", orderBO.getOrderCode());
                orderFacade.update(new OrderParam().setSubStatus(OrderConsts.OrderSubStatus.WAIT_EXPRESS.getCode()),
                        new OrderQuery().setOrderCode(excelPlatformVO.getOrderCode()));
            }
        } else {
            orderFacade.update(new OrderParam().setSubStatus(OrderConsts.OrderSubStatus.WAIT_SALES_ERROR.getCode()), new OrderQuery().setOrderCode(excelPlatformVO.getOrderCode()));
        }
    }

    private void createJkyStockIn(OrderBO orderBO, RouteSubscribeBO routeSubscribeBO, String warehouseNo, Integer quantity) {
        try {
            jkyTemplate.stockInAndDelivery(builderJkyStockInAndDelivery(orderBO, routeSubscribeBO, warehouseNo, quantity));
        } catch (Exception e) {
            log.error("订单号：{}，吉客云入库发货失败：{}", orderBO.getOrderCode(), e.getMessage(), e);
        }
    }

    private JkyStockInAndDeliveryParam builderJkyStockInAndDelivery(OrderBO orderBO, RouteSubscribeBO routeSubscribeBO,
                                                                    String warehouseNo, Integer quantity) {
        return new JkyStockInAndDeliveryParam()
                .setStockInParam(builderJkyStockIn(orderBO, warehouseNo, quantity))
                .setSnReportParam(builderJkySnReport(orderBO, warehouseNo))
                .setLogisticsUpdateParam(builderJkyLogisticsUpdate(orderBO, routeSubscribeBO));
    }

    private StockCreateAndStockInParam builderJkyStockIn(OrderBO orderBO, String warehouseNo, Integer quantity) {
        return new StockCreateAndStockInParam().setWarehouseCode(warehouseNo).setGoodsCode(orderBO.getSkuCode()).setQuantity(quantity).setBatchNo(orderBO.getOrderCode());
    }

    private SnReportParam builderJkySnReport(OrderBO orderBO, String warehouseNo) {
        SnReportParam param = new SnReportParam();
        param.setDeliveryorderno(StrUtil.blankToDefault(orderBO.getErpOrderId(), orderBO.getOrderCode()));
        param.setDeliverytype("JH_01");
        param.setOwnerorderno(StrUtil.blankToDefault(orderBO.getOriginalOrderId(), orderBO.getOrderCode()));
        param.setWarehousecode(warehouseNo);
        param.setOwnercode(warehouseNo);
        param.setOutbizcode(orderBO.getOrderCode());
        param.setOperatorcode("system");
        param.setOperatorname("system");
        param.setOperatetime(DateUtil.now());
        param.setGoods(Collections.singletonList(builderJkySnGoods(orderBO)));
        return param;
    }

    private SnReportParam.SnReportGoods builderJkySnGoods(OrderBO orderBO) {
        List<ImeiBO> list = imeiFacade.list(new ImeiQuery().setOrderId(orderBO.getOrderCode()));
        List<String> snList = list.stream().map(item -> Objects.equals("小米", orderBO.getBrand()) ? item.getImel() : item.getSn()).collect(Collectors.toList());
        SnReportParam.SnReportGoods goods = new SnReportParam.SnReportGoods();
        goods.setOutSkuCode(orderBO.getSkuCode());
        goods.setName(StrUtil.blankToDefault(orderBO.getProductName(), orderBO.getSkuName()));
        goods.setItemid(orderBO.getSkuCode());
        goods.setUnit("台");
        goods.setSnlist(snList);
        return goods;
    }

    private LogisticsUpdateParam builderJkyLogisticsUpdate(OrderBO orderBO, RouteSubscribeBO routeSubscribeBO) {
        LogisticsUpdateParam.LogisticsUpdateItem item = new LogisticsUpdateParam.LogisticsUpdateItem();
        item.setOrderNo(StrUtil.blankToDefault(orderBO.getErpOrderId(), orderBO.getOrderCode()));
        item.setLogisticNo(routeSubscribeBO.getLogisticsNo());
        item.setLogisticName(DictUtils.getDictLabel(DictDataConsts.P_EXPRESS_COMPANY, routeSubscribeBO.getLogisticsCode()));
        item.setLogisticCode(routeSubscribeBO.getLogisticsCode());
        LogisticsUpdateParam param = new LogisticsUpdateParam();
        param.setBizdata(Collections.singletonList(item));
        return param;
    }

    /**
     * 创建入库单
     *
     * @param orderBO
     * @return
     */
    private StockInInfoParam builderStockIn(OrderBO orderBO) {
        List<ImeiBO> list = imeiFacade.list(new ImeiQuery().setOrderId(orderBO.getOrderCode()));
        List<String> imeiList = null;
        if (Objects.equals("小米", orderBO.getBrand())) {
            imeiList = list.stream().map(ImeiBO::getImel).collect(Collectors.toList());
        } else {
            imeiList = list.stream().map(ImeiBO::getSn).collect(Collectors.toList());
        }


        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        String companyName = tradeOrderBO.getTradeNickName();

        StockInInfoGoodsList stockInInfoGoodsList = StockInInfoGoodsList.builder().spec_no(orderBO.getSkuCode()).num(new BigDecimal(orderBO.getQuantity()))
                .remark(String.format("代发订单号:%s,供应商名称:%s", orderBO.getOrderCode(), companyName)).stockin_price(tradeOrderBO.getTradePrice()).sn_list(imeiList).build();
        return StockInInfoParam.builder().outer_no(orderBO.getOrderCode()).warehouse_no(warehouseNo)
                .is_check(1).goods_list(Collections.singletonList(stockInInfoGoodsList)).build();
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
