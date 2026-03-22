package com.ruoyi.web.vo.order;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.web.vo.bill.converter.AccountingConvert;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliveryEndOrderVO {


    @ExcelProperty(value = "订单编号")
    private String orderCode;

    @ExcelProperty(value = "付款主体简称")

    private String payerName;


    @ExcelProperty(value = "抢单人")
    private String tradeUserName;

    @ExcelProperty(value = "品牌")

    private String brand;

    @ExcelProperty(value = "品类")

    private String category;

    @ExcelProperty(value = "商品名称")

    private String productName;

    @ExcelProperty(value = "规格信息")

    private String skuName;

    @ExcelProperty(value = "数量")

    private Integer quantity;



    @ExcelProperty(value = "收件人")
    private String addressee;


    @ExcelProperty(value = "手机号")
    private String phone;


    @ExcelProperty(value = "收货地址")
    private String receivingAddress;



    @ExcelProperty(value = "成交价")
    private BigDecimal tradePrice;

//
//    @ExcelProperty(value = "账期", converter = AccountingConvert.class)
//    private Integer accountingPeriod;

    @ExcelProperty(value = "物流单号")

    private String trackingNumber;

    @ExcelProperty(value = "sn")

    private String sn;

    @ExcelProperty(value = "86")

    private String imei;

}
