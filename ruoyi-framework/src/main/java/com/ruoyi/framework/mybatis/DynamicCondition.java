package com.ruoyi.framework.mybatis;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.utils.bean.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * 动态条件构造器 - 通过注解自动构建查询条件
 *
 * @author ruoyi
 */
@Slf4j
public class DynamicCondition {

    /**
     * 操作符枚举
     */
    public enum Operator {
        /** 等于 */
        EQ,
        /** 枚举等于 */
        ENUM_EQ,
        /** 不等于 */
        NE,
        /** 模糊匹配 */
        LIKE,
        /** 左模糊 */
        LIKE_LEFT,
        /** 右模糊 */
        LIKE_RIGHT,
        /** 大于 */
        GT,
        /** 小于 */
        LT,
        /** 大于等于 */
        GTE,
        /** 小于等于 */
        LTE,
        /** 区间 */
        BETWEEN,
        /** 不为空 */
        NOT_NULL,
        /** 为空 */
        IS_NULL,
        /** 分组 */
        GROUP,
        /** IN 查询 */
        IN,
        /** NOT IN 查询 */
        NOT_IN,
        /** FIND_IN_SET */
        FIND_IN_SET,
        /** LIMIT */
        LIMIT
    }

    /**
     * Between区间查询参数
     *
     * @param <T> 数值类型
     */
    @lombok.Data
    public static class BetweenQueryAttribute<T extends Comparable<T>> {
        private T start;
        private T end;
    }

    /**
     * 查询条件包装
     */
    @lombok.Data
    @lombok.Builder
    public static class QueryCondition {
        /**
         * 数据库字段名
         */
        private String fieldName;
        /**
         * 查询操作符
         */
        private Operator operator;
        /**
         * 是否忽略空值不查询
         */
        private boolean ignoreEmpty;
        /**
         * 是否忽略该字段
         */
        private boolean ignore;

        public static QueryCondition of(QueryField annotation) {
            return QueryCondition.builder()
                .fieldName(annotation.field())
                .operator(annotation.operator())
                .ignoreEmpty(annotation.queryEmpty())
                .ignore(annotation.ignore())
                .build();
        }

        public static QueryCondition defaultCondition() {
            return QueryCondition.builder()
                .operator(Operator.EQ)
                .ignoreEmpty(true)
                .ignore(false)
                .build();
        }
    }

    /**
     * 构建带排序的查询Wrapper
     *
     * @param query 查询参数对象
     * @param sort  排序参数
     * @param <T>   实体类型
     * @return 查询Wrapper
     */
    public static <T> Wrapper<T> toWrapper(final Object query, final SortBy sort) {
        QueryWrapper<T> wrapper = Wrappers.query();
        if (ObjectUtil.isEmpty(query)) {
            return wrapper;
        }

        // 获取所有字段（排除父类PageParamV2中的字段）
        List<Field> fields = ReflectUtils.getDeclaredFields(query.getClass(), PageParamV2.class);

        Object value;
        QueryCondition cond;
        for (Field field : fields) {
            value = ReflectUtils.getFieldValue(query, field.getName());
            if (value == null || StrUtil.isBlank(value.toString())) {
                continue;
            }


            // 根据属性上的注解获取查询条件
            cond = field.isAnnotationPresent(QueryField.class)
                ? QueryCondition.of(field.getAnnotation(QueryField.class))
                : QueryCondition.defaultCondition();

            // 属性值为空字符串判断
            boolean isBlank = field.getType() == String.class && StringUtils.isBlank((String) value);
            if (cond.isIgnore() || (!cond.isIgnoreEmpty() && isBlank)) {
                continue;
            }

            // 如果注解没有指定字段名，使用字段名
            if (StringUtils.isBlank(cond.getFieldName())) {
                cond.setFieldName(field.getName());
            }

            // 驼峰转下划线
            cond.setFieldName(StringUtils.camelToUnderline(cond.getFieldName()));

            // 根据操作符构建查询条件
            switch (cond.getOperator()) {
                case EQ:
                case ENUM_EQ:
                    wrapper.eq(cond.getFieldName(), value);
                    break;
                case NE:
                    wrapper.ne(cond.getFieldName(), value);
                    break;
                case LIKE:
                    wrapper.like(cond.getFieldName(), value);
                    break;
                case LIKE_LEFT:
                    wrapper.likeLeft(cond.getFieldName(), value);
                    break;
                case LIKE_RIGHT:
                    wrapper.likeRight(cond.getFieldName(), value);
                    break;
                case GT:
                    wrapper.gt(cond.getFieldName(), value);
                    break;
                case LT:
                    wrapper.lt(cond.getFieldName(), value);
                    break;
                case GTE:
                    wrapper.ge(cond.getFieldName(), value);
                    break;
                case LTE:
                    wrapper.le(cond.getFieldName(), value);
                    break;
                case BETWEEN:
                    if (value instanceof BetweenQueryAttribute<?>) {
                        BetweenQueryAttribute<?> between = (BetweenQueryAttribute<?>) value;
                        wrapper.between(cond.getFieldName(), between.getStart(), between.getEnd());
                    }
                    break;
                case NOT_NULL:
                    wrapper.isNotNull(cond.getFieldName());
                    break;
                case IS_NULL:
                    wrapper.isNull(cond.getFieldName());
                    break;
                case GROUP:
                    if (value instanceof String) {
                        wrapper.select((String) value);
                        wrapper.groupBy((String) value);
                    }
                    break;
                case IN:
                    if (ObjectUtil.isNotEmpty(value)) {
                        if (value instanceof Collection) {
                            wrapper.in(cond.getFieldName(), (Collection<?>) value);
                        } else if (value.getClass().isArray()) {
                            wrapper.in(cond.getFieldName(), (Object[]) value);
                        } else {
                            wrapper.in(cond.getFieldName(), value);
                        }
                    }
                    break;
                case NOT_IN:
                    if (ObjectUtil.isNotEmpty(value)) {
                        if (value instanceof Collection) {
                            wrapper.notIn(cond.getFieldName(), (Collection<?>) value);
                        } else if (value.getClass().isArray()) {
                            wrapper.notIn(cond.getFieldName(), (Object[]) value);
                        } else {
                            wrapper.notIn(cond.getFieldName(), value);
                        }
                    }
                    break;
                case FIND_IN_SET:
                    wrapper.apply(String.format(" FIND_IN_SET(%s, %s) ", value, cond.getFieldName()));
                    break;
                case LIMIT:
                    if (value instanceof Integer || value instanceof Long) {
                        wrapper.last(" LIMIT " + value);
                    }
                    break;
                default:
                    break;
            }
        }

        // 处理排序
        if (sort != null && !sort.isEmpty()) {
            for (SortBy.Field order : sort.getOrders()) {
                if (StringUtils.isNotBlank(order.getProperty())) {
                    // 驼峰转下划线
                    String column = StringUtils.camelToUnderline(order.getProperty());
                    if (SortBy.Direction.DESC == order.getDirection()) {
                        wrapper.orderByDesc(column);
                    } else {
                        wrapper.orderByAsc(column);
                    }
                }
            }
        }

        return wrapper;
    }

    /**
     * 构建查询Wrapper（无排序）
     *
     * @param query 查询参数对象
     * @param <T>   实体类型
     * @return 查询Wrapper
     */
    public static <T> Wrapper<T> toWrapper(final Object query) {
        return toWrapper(query, null);
    }
}
