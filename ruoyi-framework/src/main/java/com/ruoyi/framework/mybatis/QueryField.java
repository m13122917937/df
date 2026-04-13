package com.ruoyi.framework.mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryField {


    DynamicCondition.Operator operator() default DynamicCondition.Operator.EQ;


    String field() default "";

    boolean queryEmpty() default false;

    boolean ignore() default false;

}
