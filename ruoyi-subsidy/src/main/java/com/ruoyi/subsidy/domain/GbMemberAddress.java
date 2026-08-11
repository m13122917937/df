package com.ruoyi.subsidy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 国补商城会员地址。
 */
@Data
@Accessors(chain = true)
@TableName("gb_member_address")
public class GbMemberAddress {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private String receiverName;
    private String receiverPhone;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String detailAddress;
    private Integer defaultAddress;
    private Date createTime;
    private Date updateTime;
}
