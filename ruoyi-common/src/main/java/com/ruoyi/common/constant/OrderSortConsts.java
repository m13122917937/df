package com.ruoyi.common.constant;

public interface OrderSortConsts {


    /**
     * 新建 , 通过最晚发货时间排序
     */
    String NEW_ORDER = "last_shipping_time , update_time desc";

    /**
     * 待发布 , 通过最晚发货时间排序
     */

    String WAIT_ORDER = "last_shipping_time , update_time desc";

    /**
     * 报价中 , 通过最晚发货时间排序
     */
    String TRADING_ORDER = "last_shipping_time , update_time desc";

    /**
     * 代发货 , 通过是否需要处理申请， 最晚发货时间排序
     */
    String DELIVERY_ING_ORDER = "handle_apply ,last_shipping_time desc";

    /**
     * 当日发货 , 通过发货时间排序
     */
    String DELIVERY_TODAY_ORDER = "shipments_time desc";
    /**
     * 在途订单 , 通过发货时间排序
     */
    String TRANSIT_ORDER = "send_time desc";

    /**
     * 订单完成,通过signed_time 倒序排序
     */
    String ENDING_AND_RECEIPT = "signed_time desc";

    /**
     * 订单完成,通过signed_time 倒序排序
     */
    String ERROR_ORDER = "send_time desc";

    /**
     * 撤销订单,通过update_time 倒序排序
     */
    String REVOKE_ORDER = "update_time desc";

    String ALL_ORDER = "create_time desc";


    /**
     * 追单时间，排序倒序
     */
    String REFUND_NOTARIZE_TIME = "refund_notarize_time desc";
}
