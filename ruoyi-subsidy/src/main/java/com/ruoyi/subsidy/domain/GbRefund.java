package com.ruoyi.subsidy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 国补商城退款单。
 */
@Data
@Accessors(chain = true)
@TableName("gb_refund")
public class GbRefund {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String refundNo;
    private BigDecimal amount;
    private String refundStatus;
    private String reason;
    private String wechatRefundId;
    private Date createTime;
    private Date refundTime;
}
