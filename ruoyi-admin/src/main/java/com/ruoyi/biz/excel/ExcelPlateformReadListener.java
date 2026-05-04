package com.ruoyi.biz.excel;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
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

    String warehouseNo;

    ITradeOrderFacade tradeOrderFacade;


    public ExcelPlateformReadListener(IImeiFacade imeiFacade, IOrderFacade orderFacade, WdtClient client, String warehouseNo, ITradeOrderFacade tradeOrderFacade) {
        this.imeiFacade = imeiFacade;
        this.orderFacade = orderFacade;
        this.wdtClient = client;
        this.warehouseNo = warehouseNo;
        this.tradeOrderFacade = tradeOrderFacade;
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

        // 修改订单状态
        if (Objects.equals(platformImei.getCode(), ImeiConsts.PlatformImei.NORMAL.getCode())) {
            orderFacade.update(new OrderParam().setStatus(OrderConsts.OrderStatus.DELIVERY_END.getCode()),
                    new OrderQuery().setOrderCode(excelPlatformVO.getOrderCode()));
            //创建入库单
            try {
                wdtClient.stockInPush(builderStockIn(orderBO));
            } catch (IOException e) {
                log.error("订单号：{}，创建入库单失败：{}", orderBO.getOrderCode(), e.getMessage());
            }
        } else {
            orderFacade.update(new OrderParam().setSubStatus(OrderConsts.OrderSubStatus.WAIT_SALES_ERROR.getCode()), new OrderQuery().setOrderCode(excelPlatformVO.getOrderCode()));
        }
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
