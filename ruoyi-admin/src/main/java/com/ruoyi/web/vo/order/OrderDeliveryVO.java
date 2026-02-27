package com.ruoyi.web.vo.order;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.BooleanEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDeliveryVO {


    @ContentStyle(hidden = BooleanEnum.TRUE)
    @ExcelIgnore
    private String orderCode;


    /**
     * 原始订单号
     */
    @ApiModelProperty("原始订单号")
    @ExcelProperty(value = "原始订单号")
    private String originalOrderId;


    /**
     * 原始订单号
     */
    @ApiModelProperty("管家订单号")
    @ExcelProperty(value = "订单编号")
    private String erpOrderId;

    /**
     * 快递单号
     */
    @ApiModelProperty("物流单号")
    @ExcelProperty(value = "物流单号")
    private String trackingNumber;

    /**
     * 快递单号
     */
    @ApiModelProperty("物流公司")
    @ExcelProperty(value = "物流公司")
    private String trackingCompany;


    /**
     * 快递单号
     */
    @ApiModelProperty("商家编码")
    @ExcelProperty(value = "商家编码")
    private String skuCode;


    /**
     * 店铺名
     */
    @ApiModelProperty("平台")
    @ExcelProperty(value = "平台")
    private String platform;

    /**
     * 店铺名
     */
    @ApiModelProperty("店铺名")
    @ExcelProperty(value = "店铺名")
    private String shopName;


    /**
     * 产品型号
     */
    @ApiModelProperty("商品名")
    @ExcelProperty(value = "商品名")
    private String productName;
    /**
     * 产品型号
     */
    @ApiModelProperty("型号")
    @ExcelProperty(value = "型号")
    private String skuName;

    /**
     * 快递单号
     */
    @ApiModelProperty("sn")
    @ExcelProperty(value = "sn")
    private String sn;


    /**
     * 快递单号
     */
    @ApiModelProperty("86")
    @ExcelProperty(value = "86")
    private String imei;

    /**
     * 快递单号
     */
    @ApiModelProperty("供应商")
    @ExcelProperty(value = "供应商")
    private String tradeCompanyName;

    /**
     * 快递单号
     */
    @ApiModelProperty("成交价")
    @ExcelProperty(value = "成交价")
    private BigDecimal tradePrice;




}
