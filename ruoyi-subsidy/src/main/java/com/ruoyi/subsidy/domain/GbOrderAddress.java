package com.ruoyi.subsidy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 国补商城订单收货地址快照。
 */
@Data
@Accessors(chain = true)
@TableName("gb_order_address")
public class GbOrderAddress {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String receiverName;
    private String receiverPhone;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String detailAddress;
}
