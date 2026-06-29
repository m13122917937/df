package com.ruoyi.job.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 同步时间跟踪工具，封装上次同步时间的读取、保存和默认回退逻辑。
 */
@Slf4j
@Component
public class SyncTimeUtil {

    @Autowired
    private RedisCache redisCache;

    /**
     * 获取上次同步时间。
     * <ul>
     *   <li>从未同步过 → 从当前时间回退 defaultOffset 个时间单位</li>
     *   <li>解析失败 → 从当前时间回退 5 分钟</li>
     * </ul>
     *
     * @param lastSyncTimeKey Redis 中保存的上次同步时间 key
     * @param defaultOffset   默认回退量
     * @param unit            时间单位（建议 ChronoUnit.HOURS / DAYS）
     * @param jobLabel        任务中文名（日志用）
     * @return 起始时间
     */
    public DateTime getStartTime(String lastSyncTimeKey, int defaultOffset, TimeUnit unit, String jobLabel) {
        String lastSyncTime = redisCache.getCacheObject(lastSyncTimeKey);
        if (StrUtil.isBlank(lastSyncTime)) {
            long millis = unit.toMillis(defaultOffset);
            return DateUtil.offsetMillisecond(DateUtil.date(), (int) -millis);
        }
        try {
            return DateUtil.parse(lastSyncTime, DatePattern.NORM_DATETIME_PATTERN);
        } catch (Exception e) {
            log.warn("{}上次同步时间解析失败：{}", jobLabel, lastSyncTime);
            return DateUtil.offsetMinute(DateUtil.date(), -5);
        }
    }

    /**
     * 保存本次同步时间。
     *
     * @param lastSyncTimeKey Redis key
     * @param syncTime        同步时间点
     */
    public void saveSyncTime(String lastSyncTimeKey, DateTime syncTime) {
        redisCache.setCacheObject(lastSyncTimeKey, DateUtil.format(syncTime, DatePattern.NORM_DATETIME_PATTERN));
    }
}
