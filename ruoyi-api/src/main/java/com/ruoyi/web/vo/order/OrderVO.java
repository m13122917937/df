package com.ruoyi.web.vo.order;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.web.vo.bill.converter.AccountingConvert;
import com.ruoyi.web.vo.order.converter.OrderStyleConvert;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)

public class OrderVO {



    @ExcelProperty(value = "订单编号")
    private String orderCode;


    @ExcelProperty(value = "吉客云单号")
    private String erpOrderId;


    @ExcelProperty(value = "订单类型", converter = OrderStyleConvert.class)
    private Integer orderStyle;

    @ExcelIgnore
    private Integer orderType;

    @ExcelProperty(value = "付款主体简称")

    private String payerName;



    @ExcelProperty(value = "抢单人")
    private String tradeUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "抢单时间")
    private Date tradeTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelIgnore

    private Date sendTime;

    @ExcelIgnore

    private Date shipmentsTime;

    @ExcelIgnore

    private Date signedTime;

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

    @ExcelIgnore

    private Long province;

    @ExcelIgnore

    private String provinceName;

    @ExcelIgnore

    private Long city;


    @ExcelIgnore
    private String cityName;


    @ExcelProperty(value = "收件人")
    private String addressee;


    @ExcelProperty(value = "手机号")
    private String phone;


    @ExcelProperty(value = "收货地址")
    private String receivingAddress;


    @ExcelProperty(value = "成交价")
    private BigDecimal tradePrice;


    @ExcelProperty(value = "账期", converter = AccountingConvert.class)
    private Integer accountingPeriod;

    @ExcelIgnore

    private String trackingNumber;

    @ExcelIgnore

    private String trackingCompany;

    @ExcelIgnore

    private Integer codeOptions;

    @ExcelIgnore

    private Integer deliveryTime;


    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ExcelIgnore
    private Date deliveryDeadline;

    @ExcelIgnore

    private String otherRequire;


    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastCompeteTime;


    @ExcelIgnore
    private String tradeUserPhone;


    @ExcelIgnore
    private Integer handleApply;


    @ExcelIgnore
    private Integer revokeType;

    @ExcelIgnore

    private Integer subStatus;


    @ExcelProperty(value = "物流公司")
    private String express;


    @ExcelProperty(value = "寄件人手机号后四位")
    private String sendPhone;

    @ExcelProperty(value = "86")

    private String snList;


    @ExcelProperty(value = "SN")

    private String imeiList;


    @ExcelProperty(value = "物流单号")
    private String expressNO;

}
