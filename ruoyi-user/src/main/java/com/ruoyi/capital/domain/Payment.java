package com.ruoyi.capital.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业支付记录对象 u_payment
 *
 * @author ruoyi
 * @date 2025-09-08
 */
@Data
@Accessors(chain = true)
@TableName("u_payment")
public class Payment {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;
    /**
     * 企业id
     */
    private Long companyId;
    /**
     * 支付单号
     */
    private String tradeNo;
    /**
     * 支付状态
     */
    private Integer tradeStates;
    /**
     * 支付渠道
     */
    private Integer tradeChannel;
    /**
     * 支付类型 1.二维码支付 2.JSAPI支付 3.小程序支付
     */
    private Integer tradeType;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 第三方单号
     */
    private String outNo;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 回调时间
     */
    private Date notifyTime;
    /**
     * 发起支付人
     */
    private Long createBy;


}
