package com.ruoyi.jky.param.delivery;

import lombok.Data;

@Data
public class DeliveryOrderParam {

    private String startCreate;

    private String endCreate;

    private Integer pageSize;

    private Integer pageIndex;

    private Integer logisticNoStatus;

    private String warehouseCode;

    private String transferOrderStartDate;

    private String transferOrderEndDate;

    private Integer onlyNeedAllInAgv;

}
