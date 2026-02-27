package com.ruoyi.biz.express.bean;

import com.ruoyi.kuaidi100.model.LogisticsRouteInfo;
import lombok.Data;

import java.util.Date;

@Data
public class LogisticsStateParam {

    /** 订单主键 */
    private String orderId;

    /** 物流单号 */
    private String logisticsNo;

    /** 快递状态，枚举见{@link} */
    private String state;

    /** 签收时间 */
    private Date signDate;

    /** 揽收时间 */
    private Date collectDate;

    /** 出发地和目的地 */
    private LogisticsRouteInfo routeInfo;

    /** 是否退货 */
    private Boolean salesReturn;
}
