package com.ruoyi;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.express.JkyStockInAndDeliveryBizService;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.express.model.bo.RouteSubscribeBO;
import com.ruoyi.express.model.query.RouteSubscribeQuery;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.query.OrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@Slf4j
@ContextConfiguration
@SpringBootTest(classes = AdminApplication.class)
public class JkyStockInTest {

    @Autowired
    private JkyStockInAndDeliveryBizService jkyStockInAndDeliveryBizService;

    @Autowired
    private IOrderFacade orderFacade;

    @Autowired
    private IRouteSubscribeFacade routeSubscribeFacade;

    /**
     * 执行吉客云入库测试，执行验货 + 入库 + 5分钟后更新物流。
     * <p>
     * 使用前请将下方 orderCode 替换为实际要测试的订单号，
     * 确保该订单在数据库中存在且已录入串码（o_imei 表有对应记录）。
     */
    @Test
    public void testCreateJkyStockIn() {
        // ========== 请替换为实际订单号 ==========
        String orderCode = "1cb9070e20119000";
        // ====================================

        OrderBO orderBO = orderFacade.getOne(new OrderQuery().setOrderCode(orderCode));
        Assert.notNull(orderBO, "订单不存在：" + orderCode);
        log.info("订单信息：orderCode={}, erpOrderId={}, skuCode={}, quantity={}, status={}",
                orderBO.getOrderCode(), orderBO.getErpOrderId(), orderBO.getSkuCode(),
                orderBO.getQuantity(), orderBO.getStatus());

        RouteSubscribeBO routeSubscribeBO = routeSubscribeFacade.getOne(
                new RouteSubscribeQuery().setOrderCode(orderCode));
        if (routeSubscribeBO == null) {
            log.warn("订单 {} 未查询到物流订阅信息，将跳过吉客云物流更新", orderCode);
        } else {
            log.info("物流订阅信息：logisticsNo={}, logisticsCode={}",
                    routeSubscribeBO.getLogisticsNo(), routeSubscribeBO.getLogisticsCode());
        }

        log.info("===== 开始执行吉客云入库，订单号：{} =====", orderCode);
        jkyStockInAndDeliveryBizService.createJkyStockIn(orderBO, routeSubscribeBO);
        log.info("===== 吉客云入库执行完成，订单号：{} =====", orderCode);
    }
}
