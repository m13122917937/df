package com.ruoyi.bill.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

import java.util.Date;

/**
 * 财务流水明细对象 f_transactions
 *
 * @author ruoyi
 * @date 2025-12-01
 */
@Data
@Accessors(chain = true)
public class TransactionsQuery {
    private static final long serialVersionUID = 1L;

    /**
     * 流水ID（序号）
     */
    private Long id;
    /**
     * 所属账户ID，外键关联 accounts.account_id
     */
    private Long accountId;

    /**
     * 所属账户ID，外键关联 accounts.account_id
     */
    @QueryField(operator = DynamicCondition.Operator.LIMIT)
    private Integer Limit;
    /**
     * 交易日期
     */
    private Date transactionDate;

    @QueryField(operator = DynamicCondition.Operator.LT, field = "transaction_date")
    private Date ltTransactionDate;

    /**
     * 交易日期
     */
    @QueryField(operator = DynamicCondition.Operator.GTE, field = "transaction_date")
    private Date transactionDateStart;

    /**
     * 交易日期
     */
    @QueryField(operator = DynamicCondition.Operator.LTE, field = "transaction_date")
    private Date transactionDateEnd;


    @QueryField(operator = DynamicCondition.Operator.GT, field = "transaction_date")
    private Date gtTransactionDate;
    /**
     * 类别，0收入，1支出
     */
    private Long category;
    /**
     * 子类别，如工资、餐饮、交通、转账等
     */
    private String subCategory;
    /**
     * 交易金额（统一用正数）
     */
    private BigDecimal amount;
    /**
     * 交易后该账户的余额（可选，建议由应用维护）
     */
    private BigDecimal balanceAfter;
    /**
     * 具体支付方式（如刷卡、扫码、转账等）
     */
    private String paymentMethod;
    /**
     * 交易对方（如“星巴克”、“张三”、“工资入账”）
     */
    private String counterparty;

    /**
     * 交易对方（如“星巴克”、“张三”、“工资入账”）
     */
    @QueryField(operator = DynamicCondition.Operator.IN, field = "counterparty")
    private String counterpartyLike;
    /**
     * 备注
     */
    private String remark;

    /**
     * $column.columnComment
     */
    @QueryField(operator = DynamicCondition.Operator.GTE, field = "created_at")
    private Date gteCreatedAt;

    /**
     * $column.columnComment
     */
    @QueryField(operator = DynamicCondition.Operator.GT, field = "created_at")
    private Date gtCreatedAt;

    /**
     * $column.columnComment
     */
    @QueryField(operator = DynamicCondition.Operator.LTE, field = "created_at")
    private Date lteCreatedAt;

    /**
     * $column.columnComment
     */
    @QueryField(operator = DynamicCondition.Operator.LT, field = "created_at")
    private Date ltCreatedAt;
    /**
     * $column.columnComment
     */
    private Date createdAt;
    /**
     * $column.columnComment
     */
    private Date updatedAt;

    private Integer deleted;

}
