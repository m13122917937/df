package com.ruoyi.capital.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业充值记录对象 u_recharge
 *
 * @author ruoyi
 * @date 2025-09-08
 */
@Data
@Accessors(chain = true)
@TableName("u_recharge")
public class Recharge {
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
