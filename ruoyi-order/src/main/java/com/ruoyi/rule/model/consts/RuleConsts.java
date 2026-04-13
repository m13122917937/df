package com.ruoyi.rule.model.consts;

import lombok.Getter;

public interface RuleConsts {

    public enum Status {
        /**
         * 订单类型
         */
        NORMAL(1, "有效"),
        /**
         * 团购订单
         */
        FAILURE(2, "失效");

        @Getter
        private final int code;
        @Getter
        private final String msg;

        Status(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }


    public enum Range {
        /**
         * 订单类型
         */
        PROVINCE(0, "省"),
        COUNTY(1, "国"),
//        SHOP(2, "店铺"),
//        PLATFORM(3, "平台");
        ;

        @Getter
        private final int code;
        @Getter
        private final String msg;

        Range(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    enum PeriodType {

        ZERO(0, "到货就款"),
        ONE(1, "1天"),
        TWO(2, "2天"),
        THREE(3, "3天"),
        FOUR(4, "4天"),
        FIVE(5, "5天");

        @Getter
        private final int code;
        @Getter
        private final String msg;

        PeriodType(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    enum CodeOptions {

        BEFORE_SHIPMENT(0, "发货前提供串码"),
        AFTER_SHIPMENT(1, "发货后提供串码"),
        NOT(2, "发货后提供串码"),
        ;

        @Getter
        private final int code;
        @Getter
        private final String msg;

        CodeOptions(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

}
