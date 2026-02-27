package com.ruoyi.biz.express.bean;

/**
 * @author nlsm
 * 物流异常信息
 */

public enum ShippedErrorStatus {
    /**
     * 物流目的地异常.
     */
    TO_ERROR(1, "物流目的地异常"),

    /** 物流发货城市异常 */
    FROM_ERROR(2, "物流发货城市异常"),

    /**
     * 物流无流转信息.
     */
    NO_EXCHANGE_ERROR(3, "物流无流转信息"),

    /** 无异常 */
    NO_ERROR(4, "无异常"),

    /** 物流无流转信息 */
    NO_SIGN_ERROR(5, "签收异常"),
    /** 快递疑难 */
    KNOTTY_ERROR(6, "疑难"),

    /** 手机号异常 */
    PHONE_ERROR(7, "手机号异常"),

    DEPOSIT_DEDUCT_ERROR(8, "保证金扣除异常"),

    SUBMIT_ERROR(9, "下单失败异常"),
    ;

    private final Integer code;
    private final String memo;

    ShippedErrorStatus(int key, String memo) {
        this.code = key;
        this.memo = memo;
    }

    public Integer getCode() {
        return code;
    }

    public String getMemo() {
        return memo;
    }

    public static ShippedErrorStatus fromCode(Integer code) {
        for (ShippedErrorStatus type : ShippedErrorStatus.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return NO_EXCHANGE_ERROR;
    }
}
