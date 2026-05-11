package com.ruoyi.web.vo.order;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.web.vo.order.converter.OrderStatusConvert;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
//@Accessors(chain = true)

public class AllOrderVO {

    /**
     * 订单编码
     */

    @ExcelProperty(value = "订单编码")
    private String orderCode;

    /**
     * 原始订单号
     */

    @ExcelProperty(value = "商家单号")
    private String originalOrderId;

    /**
     * 原始订单号
     */

    @ExcelProperty(value = "旺店通订单号")
    private String erpOrderId;

    /**
     * 省
     */
    @ExcelIgnore()

    private Long province;

    /**
     * 省
     */

    @ExcelIgnore()
    private String provinceName;

    /**
     * 市
     */
    @ExcelIgnore()

    private Long city;

    /**
     * 市
     */
    @ExcelIgnore()

    private String cityName;


    /**
     * 收件人
     */

    @ExcelProperty(value = "收件人")
    private String addressee;
    /**
     * 手机号
     */

    @ExcelProperty(value = "手机号")
    private String phone;
    /**
     * 收货地址
     */

    @ExcelProperty(value = "收货地址")
    private String receivingAddress;

    /**
     * 平台
     */


    @ExcelProperty(value = "平台")
    private String platform;


    /**
     * 店铺名
     */

    @ExcelProperty(value = "店铺名")
    private String shopName;

    /**
     * 品牌
     */

    @ExcelProperty(value = "品牌")
    private String brand;

    /**
     * 类别
     */

    @ExcelProperty(value = "类别")
    private String category;


    /**
     * 产品型号
     */

    @ExcelProperty(value = "商品名")
    private String productName;
    /**
     * 产品型号
     */

    @ExcelProperty(value = "产品型号")
    private String skuName;
    /**
     * sku编码
     */

    @ExcelIgnore()
//    @ExcelProperty(value = "sku编码")
    private String skuCode;
    /**
     * 数量
     */

    @ExcelProperty(value = "数量")
    private Long quantity;




    @ExcelProperty(value = "订单状态", converter = OrderStatusConvert.class)
    private Integer status;

    @ExcelIgnore
    private Integer orderType;

    /**
     * 数量
     */

    @ExcelProperty(value = "数量")
    private BigDecimal tradePrice;


    @ExcelProperty(value = "抢单企业名称")
    private String companyName;

    /**
     * 最晚发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "交易时间")
    private Date erpTradeTime;

    /**
     * 最晚发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    @ExcelProperty(value = "最晚发货时间")
    private Date lastShippingTime;


    /**
     * 最晚发货时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    @ExcelProperty(value = "发货时间")
    private Date sendTime;


    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    @ExcelProperty(value = "签收时间")
    private Date signTime;

    /**
     * 串码
     */
    @ExcelProperty(value = "串码")
    private String imei;

}
