package com.ruoyi.bill.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

import java.util.Date;

/**
 * 【请填写功能名称】对象 f_deduction
 *
 * @author ruoyi
 * @date 2025-12-10
 */
@Data
@Accessors(chain = true)
public class DeductionParam {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;
    /**
     * 供应商id
     */
    private Long companyId;

    /**
     * 供应商名称
     */
    private String companyName;
    /**
     * 订单号
     */
    private String orderCode;
    /**
     * 旺店通id
     */
    private String erpId;
    /**
     * 商家单号
     */
    private String originalOrderId;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 品类
     */
    private String category;
    /**
     * 商品名
     */
    private String spuName;
    /**
     *
     *
     */
    private String skuName;
    /**
     * 发货时间
     */
    private Date sendTime;
    /**
     * 订单金额
     */
    private BigDecimal tradePrice;
    /**
     * 扣罚金额
     */
    private BigDecimal amount;
    /**
     * 错误原因
     */
    private String remark;

    /**
     * 状态 ， 0 已经扣罚 ， 1 已经撤销
     */
    private Integer status;

//    /**
//     * 扣罚金额
//     */
//    private BigDecimal deductionAmount;
    /**
     * $column.columnComment
     */
    private Date createTime;
    /**
     * $column.columnComment
     */
    private Long createBy;


}
