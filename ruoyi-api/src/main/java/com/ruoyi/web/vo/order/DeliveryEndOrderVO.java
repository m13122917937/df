package com.ruoyi.web.vo.order;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.web.vo.bill.converter.AccountingConvert;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliveryEndOrderVO {

    @ApiModelProperty("订单编号")
    @ExcelProperty(value = "订单编号")
    private String orderCode;

    @ExcelProperty(value = "付款主体简称")
    @ApiModelProperty("付款主体简称")
    private String payerName;

    @ApiModelProperty("抢单人")
    @ExcelProperty(value = "抢单人")
    private String tradeUserName;

    @ExcelProperty(value = "品牌")
    @ApiModelProperty("品牌")
    private String brand;

    @ExcelProperty(value = "品类")
    @ApiModelProperty("分类")
    private String category;

    @ExcelProperty(value = "商品名称")
    @ApiModelProperty("商品名称")
    private String productName;

    @ExcelProperty(value = "规格信息")
    @ApiModelProperty("SKU名称")
    private String skuName;

    @ExcelProperty(value = "数量")
    @ApiModelProperty("数量")
    private Integer quantity;


    @ApiModelProperty("收件人")
    @ExcelProperty(value = "收件人")
    private String addressee;

    @ApiModelProperty("手机号")
    @ExcelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty("收货地址")
    @ExcelProperty(value = "收货地址")
    private String receivingAddress;


    @ApiModelProperty("交易价格")
    @ExcelProperty(value = "成交价")
    private BigDecimal tradePrice;

//    @ApiModelProperty("账期")
//    @ExcelProperty(value = "账期", converter = AccountingConvert.class)
//    private Integer accountingPeriod;

    @ExcelProperty(value = "物流单号")
    @ApiModelProperty("物流单号")
    private String trackingNumber;

    @ExcelProperty(value = "sn")
    @ApiModelProperty("sn")
    private String sn;

    @ExcelProperty(value = "86")
    @ApiModelProperty("86")
    private String imei;

}
