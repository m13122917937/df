package com.ruoyi.subsidy.model.bo;

import lombok.Data;

/** 国补商城后台概览数据。 */
@Data
public class GbOverviewBO {
    private Long productCount;
    private Long pendingShipmentCount;
    private Long pendingRefundCount;
}
