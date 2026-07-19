package com.ruoyi.analysis.job;

import com.ruoyi.analysis.facade.AnalysisSyncFacade;
import com.ruoyi.common.core.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 每日经营分析同步任务。
 */
@Slf4j
@Component("analysisDailySyncJob")
public class AnalysisDailySyncJob {
    private static final String LOCK_KEY = "analysis:daily-sync:lock";

    @Autowired
    private AnalysisSyncFacade syncFacade;
    @Autowired
    private RedisCache redisCache;

    /**
     * 由若依任务中心每日02:00调用，同步前一自然日。
     */
    public void execute() {
        redisCache.tryLockRun(LOCK_KEY, 2L, TimeUnit.HOURS, "经营分析每日同步", () -> {
            log.info("开始执行经营分析每日同步");
            syncFacade.syncScheduled();
            log.info("经营分析每日同步执行完成");
        });
    }
}
