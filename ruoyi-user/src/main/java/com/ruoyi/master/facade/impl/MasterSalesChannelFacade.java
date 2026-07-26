package com.ruoyi.master.facade.impl;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.sales.SalesChannelQueryParam;
import com.ruoyi.jky.rep.sales.SalesChannelDataRep;
import com.ruoyi.jky.rep.sales.SalesChannelRep;
import com.ruoyi.jky.util.JkyResponseUtil;
import com.ruoyi.master.convert.MasterSalesChannelConvert;
import com.ruoyi.master.domain.MasterSalesChannel;
import com.ruoyi.master.facade.IMasterSalesChannelFacade;
import com.ruoyi.master.model.bo.MasterSalesChannelBO;
import com.ruoyi.master.model.param.MasterSalesChannelDepositParam;
import com.ruoyi.master.model.query.MasterSalesChannelQuery;
import com.ruoyi.master.service.MasterSalesChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 销售渠道主数据领域对外接口实现。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MasterSalesChannelFacade implements IMasterSalesChannelFacade {

    private static final String SALES_CHANNEL_SYNC_LOCK = "fy:master:sales-channel:sync:lock";
    private static final String LAST_SUCCESS_TIME_KEY = "fy:master:sales-channel:last-success-time";
    private static final int PAGE_SIZE = 100;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MasterSalesChannelService masterSalesChannelService;
    private final JkyTemplate jkyTemplate;
    private final RedisCache redisCache;

    /** {@inheritDoc} */
    @Override
    public PageBO<MasterSalesChannelBO> page(final MasterSalesChannelQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<MasterSalesChannel> channels = masterSalesChannelService.list(
                DynamicCondition.toWrapper(query, SortBy.of("-updated_time,-id")));
        return PageUtils.fromList(channels, MasterSalesChannelConvert.INSTANCE::toBOList);
    }

    /** {@inheritDoc} */
    @Override
    public void updateDeposit(final MasterSalesChannelDepositParam param) {
        masterSalesChannelService.updateDeposit(param);
    }

    /** {@inheritDoc} */
    @Override
    public void syncSalesChannels() {
        redisCache.tryLockRun(SALES_CHANNEL_SYNC_LOCK, 30L, TimeUnit.MINUTES,
                "吉客云销售渠道同步", this::doSyncSalesChannels);
    }

    private void doSyncSalesChannels() {
        LocalDateTime syncEndTime = LocalDateTime.now();
        LocalDateTime syncStartTime = getIncrementStartTime();
        int total = syncPages(syncStartTime, syncEndTime);
        redisCache.setCacheObject(LAST_SUCCESS_TIME_KEY, TIME_FORMATTER.format(syncEndTime));
        log.info("吉客云销售渠道同步完成，写入 {} 条，增量起始时间={}", total, syncStartTime);
    }

    private LocalDateTime getIncrementStartTime() {
        String lastSuccessTime = redisCache.getCacheObject(LAST_SUCCESS_TIME_KEY);
        if (lastSuccessTime == null || lastSuccessTime.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(lastSuccessTime, TIME_FORMATTER).minusMinutes(5);
    }

    private int syncPages(final LocalDateTime syncStartTime, final LocalDateTime syncEndTime) {
        int total = 0;
        for (int pageIndex = 0; ; pageIndex++) {
            List<SalesChannelRep> page = queryPage(pageIndex, syncStartTime, syncEndTime);
            total += syncPage(page, syncEndTime);
            if (page.size() < PAGE_SIZE) {
                return total;
            }
        }
    }

    private List<SalesChannelRep> queryPage(final int pageIndex, final LocalDateTime syncStartTime,
                                             final LocalDateTime syncEndTime) {
        SalesChannelQueryParam param = new SalesChannelQueryParam().setPageIndex(pageIndex).setPageSize(PAGE_SIZE);
        if (syncStartTime != null) {
            param.setGmtModifiedStart(TIME_FORMATTER.format(syncStartTime));
            param.setGmtModifiedEnd(TIME_FORMATTER.format(syncEndTime));
        }
        JkyResponse<SalesChannelDataRep> response = jkyTemplate.querySalesChannels(param);
        if (!JkyResponseUtil.isSuccess(response)) {
            throw new ServiceException("吉客云销售渠道同步失败：" + getResponseMessage(response));
        }
        SalesChannelDataRep data = JkyResponseUtil.getData(response);
        return data == null || data.getSalesChannelInfo() == null ? List.of() : data.getSalesChannelInfo();
    }

    private String getResponseMessage(final JkyResponse<SalesChannelDataRep> response) {
        return response == null ? "接口未返回响应" : response.getMsg();
    }

    private int syncPage(final List<SalesChannelRep> sources, final LocalDateTime syncTime) {
        List<MasterSalesChannel> channels = new ArrayList<>();
        for (SalesChannelRep source : sources) {
            if (source != null && source.getChannelId() != null) {
                channels.add(toDomain(source, syncTime));
            }
        }
        if (!channels.isEmpty()) {
            masterSalesChannelService.upsertPage(channels);
        }
        return channels.size();
    }

    private MasterSalesChannel toDomain(final SalesChannelRep source, final LocalDateTime syncTime) {
        MasterSalesChannel channel = MasterSalesChannelConvert.INSTANCE.toDomain(source);
        channel.setLastSyncTime(syncTime);
        channel.setCreatedTime(syncTime);
        channel.setUpdatedTime(syncTime);
        return channel;
    }
}
