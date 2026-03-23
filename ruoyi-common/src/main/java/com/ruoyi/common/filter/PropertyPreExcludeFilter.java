package com.ruoyi.common.filter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 排除JSON敏感属性
 *
 * @author ruoyi
 */
public class PropertyPreExcludeFilter extends StdSerializer<Object> implements ContextualSerializer {

    private static final long serialVersionUID = 1L;

    private Set<String> excludes = new HashSet<>();

    public PropertyPreExcludeFilter() {
        super(Object.class);
    }

    public PropertyPreExcludeFilter addExcludes(String... filters) {
        for (String filter : filters) {
            this.excludes.add(filter);
        }
        return this;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        BeanProperty property = (BeanProperty) gen.getCurrentValue();
        if (property != null) {
            // 处理属性级别的过滤
            serializeFields(value, gen, provider);
        } else {
            // 处理根级别对象
            serializeFields(value, gen, provider);
        }
        gen.writeEndObject();
    }

    private void serializeFields(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        java.lang.reflect.Field[] fields = value.getClass().getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if (!excludes.contains(field.getName())) {
                field.setAccessible(true);
                try {
                    Object fieldValue = field.get(value);
                    gen.writeObjectField(field.getName(), fieldValue);
                } catch (IllegalAccessException e) {
                    // Ignore inaccessible fields
                }
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        return this;
    }

    public Set<String> getExcludes() {
        return excludes;
    }
}
