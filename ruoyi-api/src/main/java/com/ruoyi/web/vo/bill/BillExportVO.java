package com.ruoyi.web.vo.bill;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.ruoyi.web.vo.bill.converter.ReversedConvert;
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
public class BillExportVO {
    private static final long serialVersionUID = 1L;

    /**
     * 内部订单号
     */

    @ExcelProperty("内部订单号")
    private String orderCode;

    /**
     * 发货日期
     */
    @ExcelProperty("发货日期")
    @DateTimeFormat("yyyy-MM-dd")

    private Date shipmentsDate;
    /**
     * 签收日期
     */
    @ExcelProperty("签收日期")
    @DateTimeFormat("yyyy-MM-dd")

    private Date signedDate;

    /**
     * 付款日期
     */
    @ExcelProperty("付款日期")

    @DateTimeFormat("yyyy-MM-dd")
    private Date payDate;

    /**
     * 通用名
     */
    @ExcelProperty("商品名")

    private String productName;
    /**
     * 通用编码(无仓平台sku编码)
     */
    @ExcelProperty("sku名称")

    private String skuName;

    /**
     * 单价
     */
    @ExcelProperty("单价")

    private BigDecimal tradePrice;
    /**
     * 数量
     */
    @ExcelProperty("数量")

    private Long quantity;
    /**
     * 账单金额
     */
    @ExcelProperty("账单金额")

    private BigDecimal billingAmount;


    /**
     * 付款企业主体
     */
    @ExcelProperty("付款企业主体")

    private String payCompanyName;


    /**
     * 0-货款账单，1-售后账单，2-扣罚账单
     */
    @ExcelProperty(value = "账单类型", converter = ReversedConvert.class)
    private Integer reversed;


}
