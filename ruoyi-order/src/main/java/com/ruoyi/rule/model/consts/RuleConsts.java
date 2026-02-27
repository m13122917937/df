package com.ruoyi.rule.model.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface RuleConsts {

    @Getter
    @AllArgsConstructor
    public enum Status {
        /**
         * 订单类型
         */
        NORMAL(1, "有效"),

        /**
         * 团购订单
         */
        FAILURE(2, "失效");

        int code;

        String msg;

    }


    @Getter
    @AllArgsConstructor
    public enum Range {
        /**
         * 订单类型
         */
        PROVINCE(0, "省"),
        COUNTY(1, "国"),
//        SHOP(2, "店铺"),
//        PLATFORM(3, "平台");
        ;

        int code;

        String msg;

    }

    @Getter
    @AllArgsConstructor
    public enum PeriodType {

        ZERO(0, "到货就款"),
        ONE(0, "1天"),
        TWO(0, "2天"),
        THREE(0, "3天"),
        FOUR(0, "4天"),
        FIVE(0, "5天");

        int code;
        String msg;
    }

    @Getter
    @AllArgsConstructor
    public enum CodeOptions {

        BEFORE_SHIPMENT(0, "发货前提供串码"),

        AFTER_SHIPMENT(1, "发货后提供串码"),

        NOT(2, "发货后提供串码"),

        ;

        int code;
        String msg;
    }


}
