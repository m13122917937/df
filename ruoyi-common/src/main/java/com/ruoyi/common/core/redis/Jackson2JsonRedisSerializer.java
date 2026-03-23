package com.ruoyi.common.core.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Redis使用Jackson2序列化
 * 简单直接的实现，依赖Jackson自动类型推断
 *
 * @author ruoyi
 */
public class Jackson2JsonRedisSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final Class<T> clazz;
    private final ObjectMapper objectMapper;

    public Jackson2JsonRedisSerializer(Class<T> clazz) {
        this.clazz = clazz;
        this.objectMapper = new ObjectMapper();
        // 基础配置
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许JSON中出现Long类型带L后缀（兼容旧fastjson格式）
        objectMapper.configure(DeserializationFeature.ALLOW_COERCION_OF_SCALARS, true);
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        try {
            String json = objectMapper.writeValueAsString(t);
            return json.getBytes(DEFAULT_CHARSET);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not serialize object: " + e.getMessage(), e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        // 清理非法的L后缀（fastjson遗留问题）
        str = str.replaceAll("(\\d+)L(?=[,}])", "$1");
        try {
            return objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            throw new SerializationException("Could not deserialize object: " + e.getMessage(), e);
        }
    }
}
