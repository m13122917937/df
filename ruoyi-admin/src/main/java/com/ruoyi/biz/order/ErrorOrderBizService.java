package com.ruoyi.biz.order;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.sys.IDictDistrictBizService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.param.stock.StockCreateAndStockInParam;
import com.ruoyi.kuaidi100.properties.ExpressProperties;
import com.ruoyi.order.facade.IHangingOrderFacade;
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
import com.ruoyi.wangdian.param.stock.StockInInfoGoodsList;
import com.ruoyi.wangdian.param.stock.StockInInfoParam;
import com.ruoyi.wangdian.utils.WdtClient;
import com.ruoyi.web.form.order.ExcelForm;
import com.ruoyi.web.vo.order.ExcelPlatformVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ErrorOrderBizService {

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    @Autowired
    IHangingOrderFacade hangingOrderFacade;

    @Autowired
    IDictDistrictBizService dictDistrictBizService;

    @Autowired
    RuleBizService ruleBizService;

    @Autowired
    IImeiFacade imeiFacade;

    @Autowired
    IRouteSubscribeFacade routeSubscribeFacade;

    @Autowired
    ExpressProperties expressProperties;

    @Autowired
    WdtClient wdtClient;

    @Autowired
    JkyTemplate jkyTemplate;

    @Autowired
    RuoYiConfig ruoYiConfig;

    /**
     * 订单列表
     *
     * @param excelForm
     * @return
     */
    public List<ExcelPlatformVO> listInfo(final ExcelForm excelForm) {

        DateTime dateTime = DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -15));
        List<OrderBO> orderBOList = orderFacade.list(new OrderQuery().setStatus(OrderConsts.OrderStatus.DELIVERY_ING.getCode()).setCreateTimeStart(dateTime)
                .setCategoryList(excelForm.getCategoryList()).setBrandSet(excelForm.getBrandSet()));
        if (CollectionUtil.isEmpty(orderBOList)) {
            return Collections.EMPTY_LIST;
        }
        List<ExcelPlatformVO> list = new ArrayList();
        for (OrderBO orderBO : orderBOList) {
            List<ImeiBO> imeiBOS = imeiFacade.list(new ImeiQuery().setOrderId(orderBO.getOrderCode()).setActivated(ImeiConsts.Activated.SUCCESS.getCode()).setPlatformImei(ImeiConsts.PlatformImei.WAIT_QUERY.getCode()));
            if (CollectionUtil.isEmpty(imeiBOS)) {
                continue;
            }

            for (ImeiBO imeiBO : imeiBOS) {

                ExcelPlatformVO excelPlatformVO = new ExcelPlatformVO();
                excelPlatformVO.setOrderCode(orderBO.getOrderCode());
                excelPlatformVO.setOriginalOrderId(orderBO.getOriginalOrderId());
                excelPlatformVO.setImei(imeiBO.getImel());
                excelPlatformVO.setSn(imeiBO.getSn());
                excelPlatformVO.setPlatform(orderBO.getPlatform());
                excelPlatformVO.setCategory(orderBO.getCategory());
                excelPlatformVO.setBrand(orderBO.getBrand());
                list.add(excelPlatformVO);
            }
        }
        return list;
    }

    /**
     * 发货中订单转当日发货
     *
     * @param orderCodeList 订单号列表
     */
    @Transactional
    public void deliveryIngToToday(final List<String> orderCodeList) {
        for (String orderCode : orderCodeList) {
            deliveryIngToToday(orderCode);
        }
    }

    private void deliveryIngToToday(final String orderCode) {
        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        Assert.notNull(orderBO, "订单不存在");
        Assert.isTrue(Objects.equals(orderBO.getStatus(), OrderConsts.OrderStatus.DELIVERY_ING.getCode()), "只有发货中订单可以转当日发货");
        imeiFacade.update(new ImeiParam().setPlatformImei(ImeiConsts.PlatformImei.NORMAL.getCode()).setPlatformTime(DateUtil.date()),
                new ImeiQuery().setOrderId(orderCode));
        orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.DELIVERY_END.getCode()).setUpdateTime(DateUtil.date()),
                new OrderQuery().setOrderCode(orderCode));
        try {
            wdtClient.stockInPush(builderStockIn(orderBO));
            createJkyStockIn(orderBO, ruoYiConfig.getWarehouseNo(), orderBO.getQuantity());
        } catch (IOException e) {
            log.error("订单号：{}，创建入库单失败：{}", orderBO.getOrderCode(), e.getMessage());
        }
    }

    private void createJkyStockIn(OrderBO orderBO, String warehouseNo, Integer quantity) {
        try {
            jkyTemplate.createAndStockIn(builderJkyStockIn(orderBO, warehouseNo, quantity));
        } catch (Exception e) {
            log.error("订单号：{}，创建吉客云入库单失败：{}", orderBO.getOrderCode(), e.getMessage(), e);
        }
    }

    private StockCreateAndStockInParam builderJkyStockIn(OrderBO orderBO, String warehouseNo, Integer quantity) {
        return new StockCreateAndStockInParam().setWarehouseCode(warehouseNo).setGoodsCode(orderBO.getSkuCode()).setQuantity(quantity).setBatchNo(orderBO.getOrderCode());
    }

    private StockInInfoParam builderStockIn(OrderBO orderBO) {
        List<ImeiBO> list = imeiFacade.list(new ImeiQuery().setOrderId(orderBO.getOrderCode()));
        List<String> imeiList;
        if (Objects.equals("小米", orderBO.getBrand())) {
            imeiList = list.stream().map(ImeiBO::getImel).collect(Collectors.toList());
        } else {
            imeiList = list.stream().map(ImeiBO::getSn).collect(Collectors.toList());
        }
        TradeOrderBO tradeOrderBO = tradeOrderFacade.getOne(new TradeOrderQuery().setOrderId(orderBO.getOrderCode()).setStatus(TradeOrderConsts.TradeStatus.SUCCESS.getCode()));
        String companyName = tradeOrderBO.getTradeNickName();
        StockInInfoGoodsList goodsList = StockInInfoGoodsList.builder().spec_no(orderBO.getSkuCode()).num(new BigDecimal(orderBO.getQuantity()))
                .remark(String.format("代发订单号:%s,供应商名称:%s", orderBO.getOrderCode(), companyName)).stockin_price(tradeOrderBO.getTradePrice()).sn_list(imeiList).build();
        return StockInInfoParam.builder().outer_no(orderBO.getOrderCode()).warehouse_no(ruoYiConfig.getWarehouseNo())
                .is_check(1).goods_list(Collections.singletonList(goodsList)).build();
    }

}
