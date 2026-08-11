package com.ruoyi.web.form.subsidy;

import lombok.Data;

/** 国补后台人工发货请求。 */
@Data
public class SubsidyShipmentForm {
    private String logisticsCompany;
    private String trackingNo;
}
