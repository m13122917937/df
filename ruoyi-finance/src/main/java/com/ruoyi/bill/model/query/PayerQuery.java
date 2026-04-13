package com.ruoyi.bill.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

import java.util.Date;

/**
 * 付款账号维护对象 f_payer
 *
 * @author ruoyi
 * @date 2025-11-07
 */
@Data
@Accessors(chain = true)
public class PayerQuery {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 付款户名
     */
    @QueryField(operator = DynamicCondition.Operator.IN, field = "pay_name")
    private String payNameLike;
    /**
     * 付款户名
     */
    private String payName;
    /**
     * 银行信息
     */
    private String bankName;
    /**
     * 银行卡号
     */
    private String payNo;


    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 是否激活， 0 激活， 1弃用
     */
    private Integer actived;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 修改人
     */
    private Long updateBy;


}
