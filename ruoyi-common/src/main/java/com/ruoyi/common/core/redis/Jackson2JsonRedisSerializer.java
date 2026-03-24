package com.ruoyi.common.core.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Redis使用Jackson2序列化
 * 解决泛型集合反序列化类型转换问题
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
        // 启用类型信息存储在 @type 属性中，这样反序列化时能正确获取类型
        this.objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        // 基础配置
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
