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
import com.ruoyi.jky.util.JkyResponseUtil;
import com.ruoyi.job.util.SyncTimeUtil;
import com.ruoyi.order.model.consts.OrderConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Autowired
    private SyncTimeUtil syncTimeUtil;

    /**
     * 同步吉客云售后订单。
     */
    public void execute() {
        redisCache.tryLockRun(AdminRedisKey.Jky.REFUND_SYNC_LOCK, 10L, TimeUnit.MINUTES, "吉客云售后订单同步", this::doSync);
    }

    private void doSync() {
        DateTime endTime = DateUtil.date();
        DateTime startTime = syncTimeUtil.getStartTime(AdminRedisKey.Jky.REFUND_LAST_SYNC_TIME, 1, TimeUnit.DAYS, "吉客云售后订单");
        log.info("开始同步吉客云售后订单定时任务，时间范围：{} - {}", DateUtil.format(startTime, DatePattern.NORM_DATETIME_PATTERN), DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
        SyncCount count = new SyncCount();
        boolean success = true;
        success = syncRefunds(startTime, endTime, count);
        if (success) {
            syncTimeUtil.saveSyncTime(AdminRedisKey.Jky.REFUND_LAST_SYNC_TIME, endTime);
        }
        log.info("结束同步吉客云售后订单定时任务，售后单{}条，处理订单{}条，跳过{}条，成功状态{}", count.refundCount, count.orderCount, count.skipCount, success);
    }

    private boolean syncRefunds(DateTime startTime, DateTime endTime, SyncCount count) {
        for (int pageIndex = 1; ; pageIndex++) {
            RefundQueryParam param = buildQueryParam(startTime, endTime, pageIndex);
            JkyResponse<RefundQueryRep> response = jkyTemplate.listRefunds(param);
            if (!JkyResponseUtil.isSuccess(response)) {
                log.warn("吉客云售后订单同步响应异常，code={}，msg={}", response == null ? null : response.getCode(), response == null ? null : response.getMsg());
                return false;
            }
            RefundQueryRep data = JkyResponseUtil.getData(response);
            if (data == null || CollectionUtil.isEmpty(data.getTradeAfterOnlineDtoArr())) {
                return true;
            }
            for (RefundQueryRep.TradeAfterOnlineWrapper wrapper : data.getTradeAfterOnlineDtoArr()) {
                syncRefund(wrapper, count);
            }
            if (data.getTradeAfterOnlineDtoArr().size() < PAGE_SIZE) {
                return true;
            }
        }
    }

    private RefundQueryParam buildQueryParam(DateTime startTime, DateTime endTime, int pageIndex) {
        RefundQueryParam.PageInfo pageInfo = new RefundQueryParam.PageInfo();
        int page = pageIndex - 1;
        pageInfo.setPageIndex(page);
        pageInfo.setPageSize(PAGE_SIZE);
        RefundQueryParam param = new RefundQueryParam();
        param.setPageInfo(pageInfo);
        param.setIsQueryCount(false);
        param.setHasQueryHistory(0);
        param.setCreateTimeBegin(DateUtil.format(startTime, DatePattern.NORM_DATETIME_PATTERN));
        param.setCreateTimeEnd(DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
        return param;
    }

    private void syncRefund(RefundQueryRep.TradeAfterOnlineWrapper wrapper, SyncCount count) {
        count.refundCount++;
        RefundQueryRep.RefundTradeRep refund = wrapper != null ? wrapper.getTradeAfterOnlineDTO() : null;
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

    private static class SyncCount {
        private int refundCount;
        private int orderCount;
        private int skipCount;
    }
}
