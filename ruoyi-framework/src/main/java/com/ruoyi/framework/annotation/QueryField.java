package com.ruoyi.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询方式 加上注解的属性为查询字段.
 *
 * @author Luo
 * @version 1.0
 * @date 2021-9-7 09:58
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryField {

    /**
     * 查询方式 默认为全匹配.
     *
     * @return Operator
     */
    Operator operator() default Operator.EQ;

    /**
     * 查询字段名 不填写默认为注解属性名.
     *
     * @return 结果
     */
    String field() default "";

    /**
     * 查询的字段名,配置后会查询指定的多个字段, 条件相同,多字段 or 查询
     * 目前仅支持MongoDB查询
     * @return
     */
    String[] orFields() default {};

    /**
     * 是否查询子文档.
     *
     * @return boolean
     */
    boolean queryInner() default false;

    /**
     * 子文档名称.
     *
     * @return 结果
     */
    String innerName() default "";

    /**
     * 是否查询空字符串.
     *
     * @return boolean
     */
    boolean queryEmpty() default false;

    /**
     * 是否忽略该字段 默认不忽略.
     *
     * @return boolean
     */
    boolean ignore() default false;

}
