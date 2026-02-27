package com.ruoyi.bill.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

public interface BillConsts {


    @Getter
    @AllArgsConstructor
    enum BillType {
        /**
         * 批量采购
         */
        BATCH_PURCHASE(1, "入仓"),
        /**
         * 一件代发
         */
        ONE_ITEM_SEND(2, "代发");

        private final Integer code;

        private final String msg;

        public static String getMsg(Integer code) {
            return Objects.requireNonNull(Arrays.stream(BillType.values()).filter(bo -> Objects.equals(bo.getCode(), code)).findFirst().orElse(null)).getMsg();
        }

    }


    @Getter
    @AllArgsConstructor
    enum TodayShow {
        /**
         * 0 不展示，1 展示
         */
        NOT_SHOW(0),
        /**
         * 1 展示
         */
        SHOW(1);


        private final Integer code;

    }


    @Getter
    @AllArgsConstructor
    public enum BillReversedType {

        FORWARD_DIRECTION(0, "货款"),
        REVERSE(1, "售后"),
        ORDER_ABATE(2, "扣罚"),
        ;


        private final Integer code;

        private final String msg;

        public static String getMsg(Integer code) {
            return Objects.requireNonNull(Arrays.stream(BillReversedType.values()).filter(bo -> Objects.equals(bo.getCode(), code)).findFirst().orElse(null)).getMsg();
        }
    }


    /**
     * 结算支付状态
     */
    public enum BillPayStatus {
        /**
         * 未支付
         */
        NO_PAY_STATUS(1, "未支付"),
        /**
         * 已支付
         */
        UNCONFIRMED_STATUS(2, "未确认"),
        /**
         * 已确认
         */
        CONFIRMED_STATUS(3, "已确认"),
        /**
         * 异常
         */
        ERROR_STATUS(4, "异常收款"),
        /**
         * 转异常
         */
        ABNORMAL_STATUS(5, "异常账单"),

        /**
         * 停款
         */
        STOP_PAY(7, "停款"),

        /**
         * 冻结
         */
        FREEZE(8, "冻结"),

        ;

        private final Integer code;
        private final String msg;

        BillPayStatus(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public static BillPayStatus fromCode(Integer code) {
            for (BillPayStatus type : BillPayStatus.values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
            return NO_PAY_STATUS;
        }
    }

}
