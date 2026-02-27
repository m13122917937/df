package com.ruoyi.order.model.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface HandingOrderConsts {


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
        FAILURE(2, "失效"),

        ;

        /**
         * 砍价订单
         */
        private Integer code;

        private String name;
    }


    @Getter
    @AllArgsConstructor
    public enum CodeOptions {

        /**
         * 订单类型
         */
        SEND_BEFORE_NEED(0, "发货之前提供串码"),
        SEND_AFTER_NEED(1, "发货后提供串码"),
        SEND_NEED(2, "不需要串码"),


        ;

        /**
         * 砍价订单
         */
        private Integer code;

        private String name;
    }


//    @Getter
//    @AllArgsConstructor
//    public enum PriceStatus {
//
//        CAN(1, "可出价"),
//        CONFIRMED(2, "待确认"),
//        FAILURE(3, "失效"),
//        SUCCESS(4, "已确定"),
//        ;
//
//        private int code;
//        private String name;
//
//    }

}
