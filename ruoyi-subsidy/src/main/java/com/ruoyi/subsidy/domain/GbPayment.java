package com.ruoyi.subsidy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 国补商城支付单。
 */
@Data
@Accessors(chain = true)
@TableName("gb_payment")
public class GbPayment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String paymentNo;
    private String wechatTransactionId;
    private BigDecimal amount;
    private String paymentStatus;
    private Date createTime;
    private Date paidTime;
}
