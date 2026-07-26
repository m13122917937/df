package com.ruoyi.master.model.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 销售渠道业务对象。
 */
@Data
public class MasterSalesChannelBO {

    private Long id;
    private Long jkyChannelId;
    private String channelCode;
    private String channelName;
    private Integer channelType;
    private String platformCode;
    private String platformName;
    private Long jkySubjectId;
    private String subjectName;
    private BigDecimal depositAmount;
    private LocalDateTime lastSyncTime;
}
