package com.ruoyi.common.core.redis;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * redis key 工具类.
 *
 * @date 2022/5/17
 */
@Slf4j
@UtilityClass
public class RedisKeyUtil {

    public static final String DELIMITER = ":";

    /**
     * 生成redis key.
     *
     * @param fields 字段
     * @return String
     */
    public static String generate(final String... fields) {
        if (fields == null || fields.length < 1) {
            throw new IllegalArgumentException("fields can not be empty");
        }
        try {
            return Arrays.stream(fields).filter(StrUtil::isNotBlank).collect(Collectors.joining(DELIMITER));
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return StrUtil.EMPTY;
    }

    /**
     * 分解redis key
     *
     * @param key redis key
     * @return
     */
    public static  List<String> split(String key) {
        return StrUtil.split(key, DELIMITER);
    }

}
