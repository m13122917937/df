package com.ruoyi.biz.contract;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.consts.AdminRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 合同编号生成器：HT{yyyyMMdd}{6位流水}
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Slf4j
@Component
public class ContractNoGenerator {

    private static final String PREFIX = "HT";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成下一个合同编号
     *
     * @return 合同编号
     */
    public String next() {
        Date now = new Date();
        String day = DateUtil.format(now, DatePattern.PURE_DATE_PATTERN);
        String key = AdminRedisKey.Contract.NO_DAILY_SEQ + day;
        Long seq = stringRedisTemplate.opsForValue().increment(key);
        if (Objects.equals(seq, 1L)) {
            stringRedisTemplate.expire(key, 2, TimeUnit.DAYS);
        }
        return PREFIX + day + String.format("%06d", seq);
    }
}
