package com.ruoyi.framework.mybatis;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.utils.bean.ReflectUtils;
import com.ruoyi.framework.annotation.QueryField;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * 动态条件.
 *
 */
@Slf4j
public class DynamicCondition {

    /**
     * 排序查询wrapper.
     *
     * @param query 查询参数
     * @param sort  排序
     * @param <T>   类型
     * @return Wrapper
     */
    public static <T> Wrapper<T> toWrapper(final Object query, final SortBy sort) {
        QueryWrapper<T> wrapper = Wrappers.query();
        if (ObjectUtil.isEmpty(query)) {
            return wrapper;
        }

        List<Field> fields = ReflectUtils.getDeclaredFields(query.getClass(), PageParamV2.class);

        Object value;
        QueryCondition cond;
        for (Field field : fields) {
            value = ReflectUtils.getFieldValue(query, field.getName());
            if (value == null) {
                continue;
            }
            // 根据属性上的注解获取查询条件
            cond = field.isAnnotationPresent(QueryField.class) ? QueryCondition.of(field.getAnnotation(QueryField.class)) : QueryCondition.defaultCondition();

            // 属性值为 空字符串 判断
            boolean isBlank = field.getType() == String.class && StringUtils.isBlank((String) value);
            if (cond.isIgnore() || (!cond.isQueryEmpty() && isBlank)) {
                continue;
            }
            if (StringUtils.isBlank(cond.getFiledName())) {
                cond.setFiledName(field.getName());
            }
            // mybatis-plus 将查询字段 属性名转为数据库列名（此处为驼峰转下划线）
            cond.setFiledName(StringUtils.camelToUnderline(cond.getFiledName()));
            switch (cond.getOperator()) {
                case EQ:
                case ENUM_EQ:
                    wrapper.eq(cond.getFiledName(), value);
                    break;
                case NE:
                    wrapper.ne(cond.getFiledName(), value);
                    break;
                case LIKE:
                    wrapper.like(cond.getFiledName(), value);
                    break;
                case LIKE_LEFT:
                    wrapper.likeLeft(cond.getFiledName(), value);
                    break;
                case LIKE_RIGHT:
                    wrapper.likeRight(cond.getFiledName(), value);
                    break;
                case GT:
                    wrapper.gt(cond.getFiledName(), value);
                    break;
                case LT:
                    wrapper.lt(cond.getFiledName(), value);
                    break;
                case GTE:
                    wrapper.ge(cond.getFiledName(), value);
                    break;
                case LTE:
                    wrapper.le(cond.getFiledName(), value);
                    break;
                case BETWEEN:
                    if (ObjectUtil.isNotEmpty(value) && value instanceof BetweenQueryAttribute<?>) {
                        BetweenQueryAttribute<?> attribute = (BetweenQueryAttribute<?>) value;
                        wrapper.between(cond.getFiledName(), attribute.getStart(), attribute.getEnd());
                    }
                    break;
                case NOT_NULL:
                    wrapper.isNotNull(cond.getFiledName());
                    break;
                case IS_NULL:
                    wrapper.isNull(cond.getFiledName());
                    break;
                case GROUP:
                    wrapper.select((String) value);
                    wrapper.groupBy((String) value);
                    break;
                case IN:
                    if (ObjectUtil.isNotEmpty(value)) {
                        if (value instanceof Collection) {
                            wrapper.in(cond.getFiledName(), (Collection<?>) value);
                        } else if (value.getClass().isArray()) {
                            wrapper.in(cond.getFiledName(), (Object[]) value);
                        } else {
                            wrapper.in(cond.getFiledName(), value);
                        }
                    }
                    break;
                case NOT_IN:
                    if (ObjectUtil.isNotEmpty(value)) {
                        if (value instanceof Collection) {
                            wrapper.notIn(cond.getFiledName(), (Collection<?>) value);
                        } else if (value.getClass().isArray()) {
                            wrapper.notIn(cond.getFiledName(), (Object[]) value);
                        } else {
                            wrapper.notIn(cond.getFiledName(), value);
                        }
                    }
                    break;
                case FIND_IN_SET:
                    wrapper.apply(" FIND_IN_SET({0}," + cond.getFiledName() + ")", value);
                    break;
                case LIMIT:
                    if (ObjectUtil.isNotEmpty(value) && (value instanceof Integer || value instanceof Long)) {
                        wrapper.last(" LIMIT " + value);
                    }
                    break;
                default:
                    break;
            }
        }

        if (sort != null && !sort.isEmpty()) {
            for (SortBy.Field order : sort.getOrders()) {
                if (StringUtils.isNotBlank(order.getProperty())) {
                    if (SortBy.Direction.DESC == order.getDirection()) {
                        wrapper.orderByDesc(order.getProperty());
                    } else {
                        wrapper.orderByAsc(order.getProperty());
                    }
                }
            }
        }
        return wrapper;
    }

    /**
     * 查询wrapper.
     *
     * @param query 查询参数
     * @param <T>   类型
     * @return Wrapper
     */
    public static <T> Wrapper<T> toWrapper(final Object query) {
        return toWrapper(query, null);
    }
}
