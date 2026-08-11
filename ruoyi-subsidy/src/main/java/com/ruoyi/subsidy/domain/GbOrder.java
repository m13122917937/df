package com.ruoyi.subsidy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 国补商城订单。
 */
@Data
@Accessors(chain = true)
@TableName("gb_order")
public class GbOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long memberId;
    private String orderStatus;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private Date expireTime;
    private Date paidTime;
    private Date shippedTime;
    private Date completedTime;
    private Date createTime;
    private Date updateTime;
}
