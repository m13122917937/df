package com.ruoyi.web.vo.bill;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.web.vo.bill.converter.CategoryConverter;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionsVO {

    /**
     * 流水ID（序号）
     */

    private String id;

    /**
     * 所属账户ID，外键关联 accounts.account_id
     */

    @ExcelIgnore
    private Long accountId;

    /**
     * 账户名称
     */

    @ExcelProperty("账户名称")
    private String accountName;

    /**
     * 交易日期
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("交易日期")
    private Date transactionDate;

    /**
     * 类别，0收入，1支出
     */

    @ExcelProperty(value = "类别", converter = CategoryConverter.class)
    private Integer category;

//    /**
//     * 类别描述
//     */
//
//    private String categoryDesc;

    /**
     * 子类别，如工资、餐饮、交通、转账等
     */

    @ExcelProperty("子类别")
    private String subCategory;

    /**
     * 交易金额（统一用正数）
     */

    @ExcelProperty("交易金额")
    private BigDecimal amount;

    /**
     * 交易后该账户的余额（可选，建议由应用维护）
     */

    @ExcelProperty("交易后该账户的余额")
    private BigDecimal balanceAfter;

    /**
     * 具体支付方式（如刷卡、扫码、转账等）
     */

    @ExcelIgnore
    private String paymentMethod;

    /**
     * 交易对方（如"星巴克"、"张三"、"工资入账"）
     */

    @ExcelProperty("交易对方")
    private String counterparty;

    /**
     * 备注
     */

    @ExcelProperty("备注")
    private String remark;

    /**
     * 创建时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelIgnore
    private Date createdAt;

    /**
     * 更新时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelIgnore
    private Date updatedAt;

}