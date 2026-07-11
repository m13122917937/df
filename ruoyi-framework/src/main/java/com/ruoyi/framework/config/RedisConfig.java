package com.ruoyi.framework.config;

import com.ruoyi.common.core.redis.RedisCache;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redis配置
 *
 * @author ruoyi
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisCache redisCache(RedissonClient redissonClient) {
        return new RedisCache(redissonClient);
    }

}
