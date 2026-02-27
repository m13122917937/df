

package com.ruoyi.kuaidi100.model.consts;

import cn.hutool.core.util.ArrayUtil;

import java.util.Objects;

public final class LogisticsStatus {
    public static final String STATE_NO = "-1";
    public static final String PHONE_ERROR = "-2";
    public static final String[] SIGN_ARRAY;
    public static final String[] VISA_ARRAY;
    public static final String[] COLLECT_ARRAY;
    public static final String[] ON_THE_WAY_ARRAY;
    public static final String[] DELIVERY_ARRAY;
    public static final String[] KNOTTY_ARRAY;

    public static boolean phone(String state) {
        return Objects.equals("-2", state);
    }

    public static boolean query(String state) {
        return Objects.equals("-1", state) || Objects.equals("-2", state);
    }

    public static boolean signed(String state) {
        return ArrayUtil.contains(SIGN_ARRAY, state);
    }

    public static boolean rejected(String state) {
        return ArrayUtil.contains(VISA_ARRAY, state);
    }

    public static boolean collected(String state) {
        return ArrayUtil.contains(COLLECT_ARRAY, state);
    }

    public static boolean onTheWay(String state) {
        return ArrayUtil.contains(ON_THE_WAY_ARRAY, state);
    }

    public static boolean delivery(String state) {
        return ArrayUtil.contains(DELIVERY_ARRAY, state);
    }

    public static boolean knotty(String state) {
        return ArrayUtil.contains(KNOTTY_ARRAY, state);
    }

    private LogisticsStatus() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        SIGN_ARRAY = new String[]{SubState.SIGN.code, SubState.SELF_SIGN.code, SubState.ERROR_SIGN.code, SubState.HOLOGRAPH_SIGN.code, SubState.CAST_ARK_SIGN.code};
        VISA_ARRAY = new String[]{SubState.ALREADY_PIN_SINGLE.code, SubState.BACK_TO_SIGN.code, SubState.VISA_REJECTED.code};
        COLLECT_ARRAY = new String[]{SubState.COLLECT.code, SubState.COLLECT_PLACE_AN_ORDER.code, SubState.COLLECT_WAIT.code, SubState.COLLECT_ALREADY.code};
        ON_THE_WAY_ARRAY = new String[]{SubState.ON_THE_WAY.code, SubState.ON_THE_WAY_TO_CITY.code, SubState.ON_THE_WAY_ARTERY.code, SubState.ON_THE_WAY_FORWARDED.code};
        DELIVERY_ARRAY = new String[]{SubState.DELIVERY.code, SubState.DELIVERY_THROW_IN.code};
        KNOTTY_ARRAY = new String[]{SubState.KNOTTY.code, SubState.KNOTTY_TIMEOUT_NO_SIGN.code, SubState.KNOTTY_TIMEOUT_NO_UPDATE.code, SubState.KNOTTY_REJECTION.code, SubState.KNOTTY_DELIVERY_ERROR.code, SubState.KNOTTY_TIMEOUT_NOT_FOUND.code, SubState.KNOTTY_CAN_NOT_CONTACT.code, SubState.KNOTTY_EXCEED_SCOPE.code, SubState.KNOTTY_RETENTION.code, SubState.KNOTTY_DAMAGED.code, SubState.KNOTTY_CHARGEBACK.code};
    }

    public static enum SubState {
        SIGN("3", "快件已签收"),
        SELF_SIGN("301", "本人签收"),
        ERROR_SIGN("302", "派件异常后签收"),
        HOLOGRAPH_SIGN("303", "代签"),
        CAST_ARK_SIGN("304", "投柜或站签收"),
        BACK_TO_SIGN("4", "退签"),
        ALREADY_PIN_SINGLE("401", "已销单"),
        VISA_REJECTED("14", "拒签"),
        COLLECT("1", "快件揽件"),
        COLLECT_PLACE_AN_ORDER("101", "已下单"),
        COLLECT_WAIT("102", "待揽收"),
        COLLECT_ALREADY("103", "已揽收"),
        ON_THE_WAY("0", "在途"),
        ON_THE_WAY_TO_CITY("1001", "到达派件城市"),
        ON_THE_WAY_ARTERY("1002", "干线"),
        ON_THE_WAY_FORWARDED("1003", "转递"),
        DELIVERY("5", "派件"),
        DELIVERY_THROW_IN("501", "投柜或驿站"),
        RETURN("6", "退回"),
        FORWARDED("7", "转投"),
        KNOTTY("2", "疑难"),
        KNOTTY_TIMEOUT_NO_SIGN("201", "超时未签收"),
        KNOTTY_TIMEOUT_NO_UPDATE("202", "超时未更新"),
        KNOTTY_REJECTION("203", "拒收"),
        KNOTTY_DELIVERY_ERROR("204", "派件异常"),
        KNOTTY_TIMEOUT_NOT_FOUND("205", "柜或驿站超时未取"),
        KNOTTY_CAN_NOT_CONTACT("206", "无法联系"),
        KNOTTY_EXCEED_SCOPE("207", "超出快递公司的服务区范围"),
        KNOTTY_RETENTION("208", "滞留"),
        KNOTTY_DAMAGED("209", "破损"),
        KNOTTY_CHARGEBACK("210", "销单");

        private final String code;
        private final String msg;

        private SubState(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String getCode() {
            return this.code;
        }

        public String getMsg() {
            return this.msg;
        }

        public static SubState fromCode(String code) {
            if (Objects.nonNull(code)) {
                for(SubState status : values()) {
                    if (status.getCode().equals(code)) {
                        return status;
                    }
                }
            }

            return null;
        }
    }
}
