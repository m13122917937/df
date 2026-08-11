package com.ruoyi.subsidy.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

/** 国补订单发货参数。 */
@Data
@Accessors(chain = true)
public class GbShipmentParam {
    private String orderNo;
    private String logisticsCompany;
    private String trackingNo;
}
