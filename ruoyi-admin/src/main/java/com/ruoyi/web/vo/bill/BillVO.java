package com.ruoyi.web.vo.bill;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.web.vo.bill.converter.AccountingConvert;
import com.ruoyi.web.vo.bill.converter.BillTypeConvert;
import com.ruoyi.web.vo.bill.converter.ReversedConvert;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 账单对象 f_bill
 *
 * @author ruoyi
 * @date 2025-11-07
 */
@Data
@Accessors(chain = true)
public class BillVO {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty("id")
    @ExcelIgnore
    private Long id;
    /**
     * 0-货款账单，1-售后账单，2-扣罚账单
     */
    @ApiModelProperty("0-货款账单，1-售后账单，2-扣罚账单")
    @ExcelProperty(index = 0, value = "账单类型", converter = ReversedConvert.class)
    private Integer reversed;

    /**
     * 账单类型（1:批量采购，2:一件代发）
     */
    @ApiModelProperty("账单类型（1:批量采购，2:一件代发）")
    @ExcelProperty(index = 1, value = "订单类型", converter = BillTypeConvert.class)
    private Integer billType;

    /**
     * 原始订单
     */
    @ApiModelProperty("原始订单")
    @ExcelProperty(index = 2, value = "原始订单")
    private String originalOrderId;

    /**
     * 内部订单号
     */
    @ApiModelProperty("内部订单号")
    @ExcelProperty(index = 3, value = "内部单号")
    private String orderCode;

    /**
     * 品牌
     */
    @ApiModelProperty("品牌")
    @ExcelProperty(index = 4, value = "品牌")
    private String brand;
    /**
     * 类别
     */
    @ApiModelProperty("类别")
    @ExcelProperty(index = 5, value = "类别")
    private String category;

    /**
     * 通用名
     */
    @ApiModelProperty("商品名")
    @ExcelProperty(index = 6, value = "商品名")

    private String productName;
    /**
     * 通用编码(无仓平台sku编码)
     */
    @ApiModelProperty("sku名称")
    @ExcelProperty(index = 7, value = "规格名")
    private String skuName;

    /**
     * 签收日期
     */
    @ApiModelProperty("签收日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty(index = 8, value = "签收日期")
    @DateTimeFormat("yyyy-MM-dd") // ← 关键：指定日期格式
    private Date signedDate;
    /**
     * 结算日期
     */
    @ApiModelProperty("结算日期")
    @ExcelProperty(index = 9, value = "结算日期")
    @DateTimeFormat("yyyy-MM-dd") // ← 关键：指定日期格式
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date settlementDate;

    /**
     * 单价
     */
    @ApiModelProperty("单价")
    @ExcelProperty(index = 10, value = "单价")
    private BigDecimal tradePrice;
    /**
     * 数量
     */
    @ApiModelProperty("数量")
    @ExcelProperty(index = 11, value = "数量")
    private Long quantity;
    /**
     * 账单金额
     */
    @ApiModelProperty("账单金额")
    @ExcelProperty(index = 12, value = "账单金额")
    private BigDecimal billingAmount;


    /**
     * 账期
     */
    @ApiModelProperty("账期,0 T+0, 1 T+1, 2 T+2")
    @ExcelProperty(index = 13, value = "账期", converter = AccountingConvert.class)
    private Integer accounting;


    @ExcelProperty(index = 14, value = "付款主体")
    private String payCompanyName;


    @ExcelProperty(index = 15, value = "付款时间")
    @DateTimeFormat("yyyy-MM-dd") // ← 关键：指定日期格式
    private Date payDate;


}
