package com.ruoyi.subsidy.model.consts;

/**
 * 国补订单状态常量。
 */
public final class GbOrderStatus {
    public static final String PENDING_PAY = "PENDING_PAY";
    public static final String PAID = "PAID";
    public static final String SHIPPED = "SHIPPED";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELED = "CANCELED";
    public static final String REFUND_APPLYING = "REFUND_APPLYING";
    public static final String REFUNDING = "REFUNDING";
    public static final String REFUNDED = "REFUNDED";
    public static final String REFUND_FAILED = "REFUND_FAILED";

    private GbOrderStatus() {
    }
}
