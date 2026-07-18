package com.ruoyi.analysis.job;

import com.ruoyi.analysis.service.AnalysisSyncService;
import com.ruoyi.common.core.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 每日经营分析同步任务。
 */
@Slf4j
@Component
public class AnalysisDailySyncJob {
    private static final String LOCK_KEY = "analysis:daily-sync:lock";

    @Autowired
    private AnalysisSyncService syncService;
    @Autowired
    private RedisCache redisCache;

    /**
     * 每日02:00同步前一自然日，不写入既有任务表。
     */
    @Scheduled(cron = "${analysis.sync-cron:0 0 2 * * ?}")
    public void execute() {
        redisCache.tryLockRun(LOCK_KEY, 2L, TimeUnit.HOURS, "经营分析每日同步", () -> {
            log.info("开始执行经营分析每日同步");
            syncService.syncYesterday();
            log.info("经营分析每日同步执行完成");
        });
    }
}
