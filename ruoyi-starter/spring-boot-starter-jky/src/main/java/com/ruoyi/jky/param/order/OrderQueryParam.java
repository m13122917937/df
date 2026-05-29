package com.ruoyi.jky.param.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderQueryParam {

    private String fields;

    private String scrollId;

    private Integer pageSize;

    private String tradeNo;

    private List<Long> tradeIds;

    private String sourceTradeNos;

    private String tradeDeliveryNo;

    private String startModified;

    private String endModified;

    private String startCreated;

    private String endCreated;

    private String startAuditTime;

    private String endAuditTime;

    private String startConsignTime;

    private String endConsignTime;

    private String startCompleteTime;

    private String endCompleteTime;

    private String startTradeTime;

    private String endTradeTime;

    private String startSigningTime;

    private String endSigningTime;

    private Integer tradeStatus;

    private List<Integer> tradeStatusList;

    private Integer tradeType;

    private List<String> shopIds;

    private List<String> warehouseIds;

    private Integer isTableSwitch;

    private String isDelete;

}
