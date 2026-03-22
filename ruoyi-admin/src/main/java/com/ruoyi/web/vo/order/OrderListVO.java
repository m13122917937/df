package com.ruoyi.web.vo.order;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.web.vo.order.converter.OrderStyleConvert;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)

public class OrderListVO {


    @ExcelIgnore
    private Integer subStatus;

    /**
     * 订单编码
     */

    @ExcelProperty(value = "内部单号")
    private String orderCode;



    /**
     * 原始订单号
     */

    @ExcelProperty(value = "原始订单号")
    private String originalOrderId;

    /**
     * 原始订单号
     */

    @ExcelProperty(value = "旺店通订单号")
    private String erpOrderId;


    @ExcelProperty(value = "订单类型", converter = OrderStyleConvert.class)
    private Integer orderStyle;


    @ExcelProperty(value = "平台")
    private String platform;



    @ExcelProperty(value = "店铺")
    private String shopName;


    /**
     * 省
     */
    @ExcelIgnore

    private Long province;

    /**
     * 省
     */

    @ExcelProperty(value = "省")
    private String provinceName;

    /**
     * 市
     */
    @ExcelIgnore

    private Long city;

    /**
     * 市
     */

    @ExcelProperty(value = "市")
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
    @ExcelIgnore

    private String skuCode;
    /**
     * 数量
     */

    @ExcelProperty(value = "数量")
    private Long quantity;

    /**
     * 最晚发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    @ExcelProperty(value = "最晚发货时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date lastShippingTime;


    /**
     * 最晚发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    @ExcelIgnore()
    private Date updateTime;


    @ExcelIgnore()
    private Integer revokeType;


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

    @ExcelIgnore
    private Date shipmentsTime;

    /**
     * 最晚发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    @ExcelIgnore
    private Date sendTime;

    /**
     * 挂单价（最高）
     */
    @ExcelIgnore

    @ExcelProperty(value = "挂单价（最高）")
    private BigDecimal priceHighest;

    /**
     * 挂单价（高）
     */

    @ExcelIgnore
    private BigDecimal priceHign;

    /**
     * 挂单价（低）
     */
    @ExcelIgnore

    private BigDecimal priceLow;

    /**
     * 挂单价4（最低价）
     */
    @ExcelIgnore

    private BigDecimal priceLowest;

    /**
     * 账期类型（1:字典；2:自定义）
     */
    @ExcelIgnore

    private Integer accountingPeriod;


    /**
     * 挂单价状态（1: 可出价；2:待确认 3.失效）
     */
    @ExcelIgnore

    private Integer priceHighestStatus;

    /**
     * 挂单价状态（1: 可出价；2:待确认 3.失效）
     */
    @ExcelIgnore

    private Integer priceHignStatus;

    /**
     * 挂单价状态（1: 可出价；2:待确认 3.失效）
     */
    @ExcelIgnore

    private Integer priceLowStatus;

    /**
     * 挂单价状态（1: 可出价；2:待确认 3.失效）
     */
    @ExcelIgnore

    private Integer priceLowestStatus;

    @ExcelIgnore

    private Integer quotationInterval;

    @ExcelIgnore

    private Integer deliveryTime;


    @ExcelIgnore

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastCompeteTime;


    @ExcelIgnore

    private BigDecimal tradePrice;

    /**
     * 抢单人姓名
     */
    @ExcelIgnore

    private String tradeUserName;

    /**
     * 抢单用户手机
     */
    @ExcelIgnore

    private String tradeUserPhone;


    /**
     * 抢单用户昵称
     */
    @ExcelIgnore

    private String tradeNickName;

    /**
     * 抢单用户昵称
     */
    @ExcelIgnore

    private String tradeCompanyName;

    /**
     * 快递单号
     */
    @ExcelIgnore

    private String trackingNumber;

    /**
     * 快递公司
     */
    @ExcelIgnore

    private String trackingCompany;

    @ExcelIgnore

    private Integer handleApply;



}
