package com.ruoyi.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.consts.AdminRedisKey;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.order.OrderQueryParam;
import com.ruoyi.jky.rep.order.OrderQueryRep;
import com.ruoyi.web.form.order.OrderAddForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component("jkyOrderSyncJob")
public class JkyOrderSyncJob {

    private static final String ORDER_FIELDS = "tradeNo,orderNo,sourceTradeNo,shopName,companyName,tradeTime,payTime,lastShipTime,sellerMemo,buyerMemo,flagNames,goodsDetail.goodsNo,goodsDetail.outerId,goodsDetail.sellCount,goodsDetail.needProcessCount,goodsDetail.isGift,scrollId";

    @Autowired
    private JkyTemplate jkyTemplate;

    @Autowired
    private OrderBizService orderBizService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 同步吉客云最近修改的订单。
     */
    public void execute() {
        Boolean locked = redisCache.setIfAbsent(AdminRedisKey.Jky.ORDER_SYNC_LOCK, DateUtil.now(), 30L, TimeUnit.MINUTES);
        if (!Boolean.TRUE.equals(locked)) {
            log.info("吉客云订单同步任务正在执行，本次跳过");
            return;
        }
        DateTime endTime = DateUtil.date();
        DateTime startTime = getStartTime(endTime);
        log.info("开始同步吉客云订单定时任务，时间范围：{} - {}", DateUtil.format(startTime, DatePattern.NORM_DATETIME_PATTERN), DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
        String scrollId = "";
        int tradeCount = 0;
        int orderCount = 0;
        int skipCount = 0;
        boolean success = true;
        try {
            for (; ; ) {
                OrderQueryParam param = buildQueryParam(startTime, endTime, scrollId);
                JkyResponse<OrderQueryRep> response = jkyTemplate.queryOrders(param);
                if (response == null || !Objects.equals(response.getCode(), 200)) {
                    success = false;
                    log.warn("吉客云订单同步响应异常，code={}，msg={}", response == null ? null : response.getCode(), response == null ? null : response.getMsg());
                    break;
                }
                OrderQueryRep data = response.getResult() == null ? null : response.getResult().getData();
                if (data == null || CollectionUtil.isEmpty(data.getTrades())) {
                    break;
                }
                for (OrderQueryRep.OrderTradeRep trade : data.getTrades()) {
                    tradeCount++;
                    SyncCount count = syncTrade(trade);
                    orderCount += count.orderCount;
                    skipCount += count.skipCount;
                }
                if (StrUtil.isBlank(data.getScrollId()) || Objects.equals(scrollId, data.getScrollId())) {
                    break;
                }
                scrollId = data.getScrollId();
            }
            if (success) {
                redisCache.setCacheObject(AdminRedisKey.Jky.ORDER_LAST_SYNC_TIME, DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
            }
        } finally {
            redisCache.deleteObject(AdminRedisKey.Jky.ORDER_SYNC_LOCK);
        }
        log.info("结束同步吉客云订单定时任务，销售单{}条，入库明细{}条，跳过{}条，成功状态{}", tradeCount, orderCount, skipCount, success);
    }

    private DateTime getStartTime(DateTime endTime) {
        String lastSyncTime = redisCache.getCacheObject(AdminRedisKey.Jky.ORDER_LAST_SYNC_TIME);
        if (StrUtil.isBlank(lastSyncTime)) {
            return DateUtil.offsetMinute(endTime, -10);
        }
        try {
            return DateUtil.parse(lastSyncTime, DatePattern.NORM_DATETIME_PATTERN);
        } catch (Exception e) {
            log.warn("吉客云订单上次同步时间解析失败：{}", lastSyncTime);
            return DateUtil.offsetMinute(endTime, -10);
        }
    }

    private OrderQueryParam buildQueryParam(DateTime startTime, DateTime endTime, String scrollId) {
        OrderQueryParam param = new OrderQueryParam();
        param.setFields(ORDER_FIELDS);
        param.setScrollId(scrollId);
        param.setPageSize(100);
        param.setStartModified(DateUtil.format(startTime, DatePattern.NORM_DATETIME_PATTERN));
        param.setEndModified(DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
        return param;
    }

    private SyncCount syncTrade(OrderQueryRep.OrderTradeRep trade) {
        SyncCount count = new SyncCount();
        if (trade == null || CollectionUtil.isEmpty(trade.getGoodsDetail())) {
            count.skipCount++;
            return count;
        }
        List<OrderQueryRep.OrderGoodsDetailRep> goodsDetail = trade.getGoodsDetail();
        int lineNo = 0;
        for (OrderQueryRep.OrderGoodsDetailRep goods : goodsDetail) {
            lineNo++;
            OrderAddForm form = buildOrderAddForm(trade, goods, goodsDetail.size(), lineNo);
            if (form == null) {
                count.skipCount++;
                continue;
            }
            try {
                orderBizService.add(form);
                count.orderCount++;
            } catch (Exception e) {
                count.skipCount++;
                log.error("吉客云订单同步异常，tradeNo={}，sourceTradeNo={}，skuCode={}", trade.getTradeNo(), trade.getSourceTradeNo(), form.getSkuCode(), e);
            }
        }
        return count;
    }

    private OrderAddForm buildOrderAddForm(OrderQueryRep.OrderTradeRep trade, OrderQueryRep.OrderGoodsDetailRep goods, int goodsSize, int lineNo) {
        if (goods == null || Objects.equals(goods.getIsGift(), 1)) {
            return null;
        }
        String tradeNo = trade.getTradeNo();
        String skuCode = StrUtil.blankToDefault(goods.getOuterId(), goods.getGoodsNo());
        Long quantity = toQuantity(goods.getNeedProcessCount(), goods.getSellCount());
        Date erpTradeTime = parseDate(StrUtil.blankToDefault(trade.getTradeTime(), trade.getPayTime()));
        Date lastShippingTime = parseDate(trade.getLastShipTime());
        String address = buildAddress(trade);
        if (StrUtil.hasBlank(tradeNo, trade.getShopName(), skuCode, address) || quantity == null || erpTradeTime == null || lastShippingTime == null) {
            log.warn("跳过吉客云订单，必要字段缺失，tradeNo={}，shopName={}，skuCode={}，quantity={}，tradeTime={}，lastShipTime={}，addressBlank={}",
                    tradeNo, trade.getShopName(), skuCode, quantity, trade.getTradeTime(), trade.getLastShipTime(), StrUtil.isBlank(address));
            return null;
        }
        OrderAddForm form = new OrderAddForm();
        form.setErpOrderId(goodsSize > 1 ? tradeNo + "-" + lineNo : tradeNo);
        form.setOriginalOrderId(firstNotBlank(trade.getSourceTradeNo(), trade.getOrderNo(), tradeNo));
        form.setShopName(trade.getShopName());
        form.setSkuCode(skuCode);
        form.setQuantity(quantity);
        form.setPlatform(StrUtil.blankToDefault(trade.getCompanyName(), "吉客云"));
        form.setErpTradeTime(erpTradeTime);
        form.setLastShippingTime(lastShippingTime);
        form.setAddress(address);
        parseAddress(form);
        form.setOrderStyle(trade.getFlagNames());
        return form;
    }

    private String buildAddress(OrderQueryRep.OrderTradeRep trade) {
        StringBuilder builder = new StringBuilder();
        appendRemark(builder, trade.getSellerMemo());
        appendRemark(builder, trade.getBuyerMemo());
        String address = builder.toString();
        return StrUtil.isBlank(address) ? null : address;
    }

    private void appendRemark(StringBuilder builder, String remark) {
        if (StrUtil.isBlank(remark)) {
            return;
        }
        if (builder.length() > 0) {
            builder.append('\n');
        }
        builder.append(remark.trim());
    }

    private void parseAddress(OrderAddForm form) {
        List<String> lines = StrUtil.splitTrim(form.getAddress(), '\n');
        if (lines.size() < 3) {
            return;
        }
        form.setAddressee(lines.get(0));
        if (Objects.equals(lines.get(1), "收起") && lines.size() >= 5) {
            form.setPhone(lines.get(3));
            form.setAddress(lines.get(4));
            return;
        }
        form.setPhone(lines.get(1));
        form.setAddress(lines.get(2));
    }

    private Long toQuantity(BigDecimal needProcessCount, BigDecimal sellCount) {
        BigDecimal quantity = needProcessCount != null && NumberUtil.isGreater(needProcessCount, BigDecimal.ZERO) ? needProcessCount : sellCount;
        if (quantity == null || !NumberUtil.isGreater(quantity, BigDecimal.ZERO)) {
            return null;
        }
        return quantity.longValue();
    }

    private Date parseDate(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        try {
            return DateUtil.parse(value, DatePattern.NORM_DATETIME_PATTERN);
        } catch (Exception e) {
            return null;
        }
    }

    private String firstNotBlank(String... values) {
        for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private static class SyncCount {
        private int orderCount;
        private int skipCount;
    }
}
