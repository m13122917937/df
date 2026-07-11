package com.ruoyi.biz.order;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.consts.AdminRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 销售退货单号生成器：TH{yyyyMMdd}{6位流水}
 *
 * @author ruoyi
 * @date 2026-06-30
 */
@Slf4j
@Component
public class SalesReturnNoGenerator {

    private static final String PREFIX = "TH";

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 生成下一个退货单号
     *
     * @return 退货单号
     */
    public String next() {
        Date now = new Date();
        String day = DateUtil.format(now, DatePattern.PURE_DATE_PATTERN);
        String key = AdminRedisKey.SalesReturn.NO_DAILY_SEQ + day;
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        long seq = atomicLong.incrementAndGet();
        if (Objects.equals(seq, 1L)) {
            atomicLong.expire(2, TimeUnit.DAYS);
        }
        return PREFIX + day + String.format("%06d", seq);
    }
}
