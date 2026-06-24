package com.ruoyi.web.form.jky;

import lombok.Data;

/**
 * 吉客云三方回调请求参数，参考 snreport 接口 bizdata 格式。
 */
@Data
public class JkyCallbackForm {

    /** 出库单号 */
    private String deliveryOrderNo;

    /** 外部订单号 */
    private String ownerOrderNo;

    /** 仓库编码 */
    private String warehouseCode;

    /** 货主编码 */
    private String ownerCode;

    /** 物流单号 */
    private String logisticNo;

    /** 物流公司名称 */
    private String logisticName;

    /** 物流公司编码 */
    private String logisticCode;

}
