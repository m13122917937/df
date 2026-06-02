package com.ruoyi.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.consts.AdminRedisKey;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.refund.RefundQueryParam;
import com.ruoyi.jky.rep.refund.RefundQueryRep;
import com.ruoyi.order.model.consts.OrderConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component("jkyRefundSyncJob")
public class JkyRefundSyncJob {

    private static final int PAGE_SIZE = 100;

    @Autowired
    private JkyTemplate jkyTemplate;

    @Autowired
    private OrderBizService orderBizService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 同步吉客云售后订单。
     */
    public void execute() {
        Boolean locked = redisCache.setIfAbsent(AdminRedisKey.Jky.REFUND_SYNC_LOCK, DateUtil.now(), 10L, TimeUnit.MINUTES);
        if (!Boolean.TRUE.equals(locked)) {
            log.info("吉客云售后订单同步任务正在执行，本次跳过");
            return;
        }
        DateTime endTime = DateUtil.date();
        DateTime startTime = getStartTime(endTime);
        log.info("开始同步吉客云售后订单定时任务，时间范围：{} - {}", DateUtil.format(startTime, DatePattern.NORM_DATETIME_PATTERN), DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
        SyncCount count = new SyncCount();
        boolean success = true;
        try {
            success = syncRefunds(startTime, endTime, count);
            if (success) {
                redisCache.setCacheObject(AdminRedisKey.Jky.REFUND_LAST_SYNC_TIME, DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
            }
        } finally {
            redisCache.deleteObject(AdminRedisKey.Jky.REFUND_SYNC_LOCK);
        }
        log.info("结束同步吉客云售后订单定时任务，售后单{}条，处理订单{}条，跳过{}条，成功状态{}", count.refundCount, count.orderCount, count.skipCount, success);
    }

    private boolean syncRefunds(DateTime startTime, DateTime endTime, SyncCount count) {
        for (int pageIndex = 1; ; pageIndex++) {
            RefundQueryParam param = buildQueryParam(startTime, endTime, pageIndex);
            JkyResponse<RefundQueryRep> response = jkyTemplate.listRefunds(param);
            if (response == null || !Objects.equals(response.getCode(), 200)) {
                log.warn("吉客云售后订单同步响应异常，code={}，msg={}", response == null ? null : response.getCode(), response == null ? null : response.getMsg());
                return false;
            }
            RefundQueryRep data = response.getResult() == null ? null : response.getResult().getData();
            if (data == null || CollectionUtil.isEmpty(data.getTradeAfterOnlineDtoArr())) {
                return true;
            }
            for (RefundQueryRep.RefundTradeRep refund : data.getTradeAfterOnlineDtoArr()) {
                syncRefund(refund, count);
            }
            if (data.getTradeAfterOnlineDtoArr().size() < PAGE_SIZE) {
                return true;
            }
        }
    }

    private RefundQueryParam buildQueryParam(DateTime startTime, DateTime endTime, int pageIndex) {
        RefundQueryParam.PageInfo pageInfo = new RefundQueryParam.PageInfo();
        pageInfo.setPageIndex(pageIndex);
        pageInfo.setPageSize(PAGE_SIZE);
        RefundQueryParam param = new RefundQueryParam();
        param.setPageInfo(pageInfo);
        param.setIsQueryCount(false);
        param.setHasQueryHistory(0);
        param.setCreateTimeBegin(DateUtil.format(startTime, DatePattern.NORM_DATETIME_PATTERN));
        param.setCreateTimeEnd(DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
        return param;
    }

    private void syncRefund(RefundQueryRep.RefundTradeRep refund, SyncCount count) {
        count.refundCount++;
        if (refund == null || StrUtil.isBlank(refund.getPlatOrderNo())) {
            count.skipCount++;
            return;
        }
        try {
            int orderCount = orderBizService.revokeJkyRefund(refund.getPlatOrderNo(), OrderConsts.RevokeType.REFUND.getCode());
            if (orderCount == 0) {
                count.skipCount++;
                return;
            }
            count.orderCount += orderCount;
        } catch (Exception e) {
            count.skipCount++;
            log.error("吉客云售后订单处理异常，refundNo={}，platOrderNo={}", refund.getRefundNo(), refund.getPlatOrderNo(), e);
        }
    }

    private DateTime getStartTime(DateTime endTime) {
        String lastSyncTime = redisCache.getCacheObject(AdminRedisKey.Jky.REFUND_LAST_SYNC_TIME);
        if (StrUtil.isBlank(lastSyncTime)) {
            return DateUtil.offsetMinute(endTime, -5);
        }
        try {
            return DateUtil.parse(lastSyncTime, DatePattern.NORM_DATETIME_PATTERN);
        } catch (Exception e) {
            log.warn("吉客云售后订单上次同步时间解析失败：{}", lastSyncTime);
            return DateUtil.offsetMinute(endTime, -5);
        }
    }

    private static class SyncCount {
        private int refundCount;
        private int orderCount;
        private int skipCount;
    }
}
