package com.ruoyi.subsidy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/** 国补商城发货信息。 */
@Data
@Accessors(chain = true)
@TableName("gb_shipment")
public class GbShipment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String logisticsCompany;
    private String trackingNo;
    private Date shippedTime;
    private Date createTime;
}
