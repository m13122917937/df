package com.ruoyi.master.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 销售渠道主数据，仅映射 m_sales_channel。
 */
@Data
@TableName("m_sales_channel")
public class MasterSalesChannel {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long jkyChannelId;
    private String channelCode;
    private String channelName;
    private Integer channelType;
    private String platformCode;
    private String platformName;
    private Long channelDepartmentId;
    private String channelDepartmentName;
    private Long jkySubjectId;
    private String subjectName;
    private String warehouseCode;
    private String warehouseName;
    private String contactName;
    private String contactPhone;
    private String email;
    private String address;
    private String countryName;
    private String provinceName;
    private String cityName;
    private String townName;
    private String memo;
    private LocalDateTime lastSyncTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
