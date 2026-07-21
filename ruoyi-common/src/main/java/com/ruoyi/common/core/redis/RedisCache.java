package com.ruoyi.common.core.redis;

import lombok.AllArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * spring redis 工具类（基于 Redisson 实现）
 *
 * @author ruoyi
 */
@Slf4j
@AllArgsConstructor
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisCache {

    public RedissonClient redissonClient;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redissonClient.getKeys().expire(key, timeout, unit);
    }

    /**
     * 获取有效时间
     *
     * @param key Redis键
     * @return 有效时间（秒）
     */
    public long getExpire(final String key) {
        return redissonClient.getKeys().remainTimeToLive(key) / 1000;
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    /**
     * 原子获取并删除缓存对象。
     *
     * @param key 缓存键
     * @param <T> 对象类型
     * @return 原缓存值，不存在时返回 null
     */
    public <T> T getAndDeleteCacheObject(final String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.getAndDelete();
    }

    /**
     * SETNX 原子操作 - 只有当 key 不存在时才设置值，key 存在则不操作
     *
     * @param key   Redis键
     * @param value 要设置的值
     * @return true 设置成功 (key不存在), false 设置失败 (key已存在)
     */
    public <T> Boolean setIfAbsent(final String key, final T value) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.trySet(value);
    }

    /**
     * SETNX 原子操作 - 只有当 key 不存在时才设置值并设置过期时间，key 存在则不操作
     *
     * @param key      Redis键
     * @param value    要设置的值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return true 设置成功 (key不存在), false 设置失败 (key已存在)
     */
    public <T> Boolean setIfAbsent(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.trySet(value, timeout, timeUnit);
    }

    /**
     * 查询 key 是否存在，不存在则设置值，存在则返回当前值
     *
     * @param key   Redis键
     * @param value 要设置的值 (当key不存在时)
     * @return key存在返回当前值，key不存在返回新设置的值
     */
    public <T> T getAndSetIfAbsent(final String key, final T value) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        boolean success = bucket.trySet(value);
        if (success) {
            return value;
        }
        return bucket.get();
    }

    /**
     * 删除单个对象
     *
     * @param key 缓存键值
     * @return 是否删除成功
     */
    public boolean deleteObject(final String key) {
        return redissonClient.getKeys().delete(key) > 0;
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return 删除数量
     */
    public long deleteObject(final Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return 0;
        }
        long count = 0;
        for (Object key : collection) {
            if (redissonClient.getBucket(key.toString()).delete()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存后的列表长度
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        RList<T> list = redissonClient.getList(key);
        list.addAll(dataList);
        return list.size();
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        return redissonClient.<T>getList(key).readAll();
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 是否成功
     */
    public <T> boolean setCacheSet(final String key, final Set<T> dataSet) {
        RSet<T> set = redissonClient.getSet(key);
        set.addAll(dataSet);
        return true;
    }

    /**
     * 获得缓存的set
     *
     * @param key 缓存键
     * @return 缓存的set集合
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redissonClient.<T>getSet(key).readAll();
    }

    /**
     * 缓存Map
     *
     * @param key     缓存键值
     * @param dataMap 缓存的数据
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        RMap<String, T> map = redissonClient.getMap(key);
        map.putAll(dataMap);
    }

    /**
     * 获得缓存的Map
     *
     * @param key 缓存键值
     * @return 缓存的Map
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return redissonClient.<String, T>getMap(key).readAllMap();
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        RMap<String, T> map = redissonClient.getMap(key);
        map.put(hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        RMap<String, T> map = redissonClient.getMap(key);
        return map.get(hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象列表
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        RMap<String, T> map = redissonClient.getMap(key);
        Set<String> stringKeys = hKeys.stream().map(Object::toString).collect(Collectors.toSet());
        return new ArrayList<>(map.getAll(stringKeys).values());
    }

    /**
     * 删除Hash中的某条数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return 是否成功
     */
    public boolean deleteCacheMapValue(final String key, final String hKey) {
        RMap<String, Object> map = redissonClient.getMap(key);
        return map.remove(hKey) != null;
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 键列表
     */
    public Collection<String> keys(final String pattern) {
        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern(pattern);
        return StreamSupport.stream(keys.spliterator(), false).collect(Collectors.toList());
    }

    /**
     * 尝试获取分布式锁，获取成功则执行 task，失败则跳过。
     *
     * @param lockKey   锁的 Redis key
     * @param leaseTime 锁持有时间（到期自动释放，防止死锁）
     * @param unit      时间单位
     * @param jobName   任务名称（日志用）
     * @param task      获取锁后执行的任务
     * @return true 任务执行成功；false 获取锁失败或被中断
     */
    public boolean tryLockRun(String lockKey, long leaseTime, TimeUnit unit, String jobName, Runnable task) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked;
        try {
            locked = lock.tryLock(0, leaseTime, unit);
        } catch (InterruptedException e) {
            log.error("{} 获取锁被中断", jobName, e);
            Thread.currentThread().interrupt();
            return false;
        }
        if (!locked) {
            log.info("{} 任务正在执行，本次跳过", jobName);
            return false;
        }
        try {
            task.run();
            return true;
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    /**
     * 获取 Redisson RLock 实例，适用于需要在循环中逐条加锁等灵活场景。
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }
}
