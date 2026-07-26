package com.ruoyi.web.vo.master;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 销售渠道列表响应。
 */
@Data
public class MasterSalesChannelVO {

    private Long id;
    private String channelCode;
    private String channelName;
    private String platformName;
    private String subjectName;
    private String warehouseName;
    private String contactName;
    private String contactPhone;
    private LocalDateTime lastSyncTime;
}
