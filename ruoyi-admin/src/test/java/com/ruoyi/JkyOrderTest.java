package com.ruoyi;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.order.OrderQueryParam;
import com.ruoyi.jky.rep.order.OrderQueryRep;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@Slf4j
@ContextConfiguration
@SpringBootTest(classes = AdminApplication.class)
public class JkyOrderTest {

    @Autowired
    private JkyTemplate jkyTemplate;

    /**
     * 拉取吉客云最近一小时修改的订单。
     */
    @Test
    public void pullJkyOrders() {
        DateTime endTime = DateUtil.date();
        DateTime startTime = DateUtil.offsetHour(endTime, -1);

        OrderQueryParam param = new OrderQueryParam();
        param.setFields("tradeNo,orderNo,shopName,companyName,warehouseName,lastShipTime,goodsDetail.goodsNo,goodsDetail.outerId,pickUpCode,expense.expenseFee,expense.expenseItemName,billDate,goodsPlatDiscountFee,goodsDetail.shareOrderDiscountFee,goodsDetail.shareOrderPlatDiscountFee,customizeGoodsColumn9,goodsDetail.goodsId,goodsDetail.sellCount,goodsDetail.needProcessCount,goodsDetail.baseUnitSellCount,goodsDetail.assessmentCost,goodsDetail.compassSourceContentTypem,shopId,warehouseId,scrollId");
        param.setScrollId("");
        param.setPageSize(10);
        param.setStartModified(DateUtil.format(startTime, DatePattern.NORM_DATETIME_PATTERN));
        param.setEndModified(DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));

        JkyResponse<OrderQueryRep> response = jkyTemplate.queryOrders(param);
        log.info("吉客云订单拉取响应：{}", JacksonUtil.toJson(response));
    }

}
