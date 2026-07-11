package com.ruoyi;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.consts.AdminRedisKey;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.order.OrderQueryParam;
import com.ruoyi.jky.param.warehouse.WarehouseListParam;
import com.ruoyi.job.JkyOrderSyncJob;
import com.ruoyi.job.JkyRefundSyncJob;
import com.ruoyi.job.JkyGoodsSyncJob;
import com.ruoyi.job.JkyVendorSyncJob;
import com.ruoyi.jky.rep.order.OrderQueryRep;
import com.ruoyi.jky.rep.warehouse.WarehouseListRep;
import com.ruoyi.job.util.SyncTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ContextConfiguration
@SpringBootTest(classes = AdminApplication.class)
public class JkyOrderTest {

    @Autowired
    private JkyTemplate jkyTemplate;

    @Autowired
    private JkyOrderSyncJob jkyOrderSyncJob;

    @Autowired
    private JkyVendorSyncJob jkyVendorSyncJob;

    @Autowired
    private JkyRefundSyncJob jkyRefundSyncJob;

    @Autowired
    private JkyGoodsSyncJob jkyGoodsSyncJob;

    @Autowired
    private RedisCache redisCache;


    /**
     * 执行吉客云订单同步定时任务。
     */
    @Test
    public void executeJkyOrderSyncJob() {
        redisCache.deleteObject(AdminRedisKey.Jky.ORDER_SYNC_LOCK);
        redisCache.deleteObject(AdminRedisKey.Jky.ORDER_LAST_SYNC_TIME);
        jkyOrderSyncJob.execute();
    }

    /**
     * 执行吉客云供应商编码同步定时任务。
     */
    @Test
    public void executeJkyVendorSyncJob() {
        redisCache.deleteObject(AdminRedisKey.Jky.VENDOR_SYNC_LOCK);
        jkyVendorSyncJob.execute();
    }

    @Autowired
    SyncTimeUtil syncTimeUtil;
    /**
     * 执行吉客云售后订单同步定时任务。
     */
    @Test
    public void executeJkyRefundSyncJob() {
        redisCache.deleteObject(AdminRedisKey.Jky.REFUND_SYNC_LOCK);
        redisCache.deleteObject(AdminRedisKey.Jky.REFUND_LAST_SYNC_TIME);
        jkyRefundSyncJob.execute();
//        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -3);
//        syncTimeUtil.saveSyncTime(AdminRedisKey.Jky.REFUND_LAST_SYNC_TIME, dateTime);
    }

    /**
     * 执行吉客云商品SKU同步定时任务。
     */
    @Test
    public void executeJkyGoodsSyncJob() {
        redisCache.deleteObject(AdminRedisKey.Jky.GOODS_SYNC_LOCK);
        redisCache.deleteObject(AdminRedisKey.Jky.GOODS_LAST_SYNC_TIME);
        jkyGoodsSyncJob.execute();
    }

    /**
     * 拉取吉客云全部仓库。
     */
    @Test
    public void pullJkyWarehouses() {
        int pageIndex = 0;
        int pageSize = 50;
        List<WarehouseListRep.WarehouseInfoRep> warehouses = new ArrayList<>();
        while (true) {
            WarehouseListParam param = new WarehouseListParam()
                    .setPageIndex(pageIndex)
                    .setPageSize(pageSize)
                    .setIncludeDeleteAndBlockup(1);
            JkyResponse<WarehouseListRep> response = jkyTemplate.warehouseList(param);
            log.info("吉客云仓库第{}页响应：{}", pageIndex, JacksonUtil.toJson(response));
            WarehouseListRep data = response.getResult() == null ? null : response.getResult().getData();
            List<WarehouseListRep.WarehouseInfoRep> warehouseInfo = data == null ? null : data.getWarehouseInfo();
            if (warehouseInfo == null || warehouseInfo.isEmpty()) {
                break;
            }
            warehouses.addAll(warehouseInfo);
            if (warehouseInfo.size() < pageSize) {
                break;
            }
            pageIndex++;
        }
        log.info("吉客云仓库总数：{}，仓库列表：{}", warehouses.size(), JacksonUtil.toJson(warehouses));
    }

    /**
     * 按定时任务参数查询吉客云订单。
     */
    @Test
    public void queryOrders() {
        DateTime endTime = DateUtil.date();
        DateTime startTime = DateUtil.offsetHour(endTime, -1);

        OrderQueryParam param = new OrderQueryParam();
        param.setFields("tradeNo,orderNo,sourceTradeNo,shopName,companyName,tradeTime,payTime,lastShipTime,payerName,payerPhone,payerAddress,sellerMemo,buyerMemo,flagNames,goodsDetail.goodsNo,goodsDetail.outerId,goodsDetail.sellCount,goodsDetail.needProcessCount,goodsDetail.isGift,scrollId");
        param.setScrollId("");
        param.setPageSize(10);
        param.setIsReturnPddData(1);
        param.setIsPddQuery(0);
        param.setStartAuditTime(DateUtil.format(startTime, DatePattern.NORM_DATETIME_PATTERN));
        param.setEndAuditTime(DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
        param.setIsDelete("0");

        JkyResponse<OrderQueryRep> response = jkyTemplate.queryOrders(param);
        log.info("吉客云订单查询响应：{}", JacksonUtil.toJson(response));
    }

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
