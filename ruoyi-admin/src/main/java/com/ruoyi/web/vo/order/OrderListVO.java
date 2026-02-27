package com.ruoyi.web.vo.order;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.web.vo.order.converter.OrderStyleConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel("新建中的订单响应")
public class OrderListVO {

    @ApiModelProperty("前置状态,  81、82 物流异常， 83、84、85 串码异常")
    @ExcelIgnore
    private Integer subStatus;

    /**
     * 订单编码
     */
    @ApiModelProperty("订单编码")
    @ExcelProperty(value = "内部单号")
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
    @ExcelProperty(value = "旺店通订单号")
    private String erpOrderId;

    @ApiModelProperty("订单类型，0 百亿 1 百亿微派")
    @ExcelProperty(value = "订单类型", converter = OrderStyleConvert.class)
    private Integer orderStyle;

    @ApiModelProperty("平台")
    @ExcelProperty(value = "平台")
    private String platform;


    @ApiModelProperty("店铺")
    @ExcelProperty(value = "店铺")
    private String shopName;


    /**
     * 省
     */
    @ExcelIgnore
    @ApiModelProperty("省ID")
    private Long province;

    /**
     * 省
     */
    @ApiModelProperty("省ID")
    @ExcelProperty(value = "省")
    private String provinceName;

    /**
     * 市
     */
    @ExcelIgnore
    @ApiModelProperty("市ID")
    private Long city;

    /**
     * 市
     */
    @ApiModelProperty("市ID")
    @ExcelProperty(value = "市")
    private String cityName;


    /**
     * 收件人
     */
    @ApiModelProperty("收件人")
    @ExcelProperty(value = "收件人")
    private String addressee;
    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    @ExcelProperty(value = "手机号")
    private String phone;
    /**
     * 收货地址
     */
    @ApiModelProperty("收货地址")
    @ExcelProperty(value = "收货地址")
    private String receivingAddress;

    /**
     * 平台
     */


    /**
     * 品牌
     */
    @ApiModelProperty("品牌")
    @ExcelProperty(value = "品牌")
    private String brand;

    /**
     * 类别
     */
    @ApiModelProperty("类别")
    @ExcelProperty(value = "类别")
    private String category;


    /**
     * 产品型号
     */
    @ApiModelProperty("产品型号")
    @ExcelProperty(value = "商品名")
    private String productName;
    /**
     * 产品型号
     */
    @ApiModelProperty("产品型号")
    @ExcelProperty(value = "产品型号")
    private String skuName;
    /**
     * sku编码
     */
    @ExcelIgnore
    @ApiModelProperty("sku编码")
    private String skuCode;
    /**
     * 数量
     */
    @ApiModelProperty("数量")
    @ExcelProperty(value = "数量")
    private Long quantity;

    /**
     * 最晚发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("最晚发货时间")
    @ExcelProperty(value = "最晚发货时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date lastShippingTime;


    /**
     * 最晚发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("更新时间")
    @ExcelIgnore()
    private Date updateTime;

    @ApiModelProperty("撤销原因")
    @ExcelIgnore()
    private Integer revokeType;


    /**
     * 最晚发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("交易时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "交易时间")
    private Date erpTradeTime;

    /**
     * 最晚发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("快递揽收时间")
    @ExcelIgnore
    private Date shipmentsTime;

    /**
     * 最晚发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("发货时间")
    @ExcelIgnore
    private Date sendTime;

    /**
     * 挂单价（最高）
     */
    @ExcelIgnore
    @ApiModelProperty("挂单价（最高）")
    @ExcelProperty(value = "挂单价（最高）")
    private BigDecimal priceHighest;

    /**
     * 挂单价（高）
     */
    @ApiModelProperty("挂单价（高）")
    @ExcelIgnore
    private BigDecimal priceHign;

    /**
     * 挂单价（低）
     */
    @ExcelIgnore
    @ApiModelProperty("挂单价（低）")
    private BigDecimal priceLow;

    /**
     * 挂单价4（最低价）
     */
    @ExcelIgnore
    @ApiModelProperty("挂单价4（最低价）")
    private BigDecimal priceLowest;

    /**
     * 账期类型（1:字典；2:自定义）
     */
    @ExcelIgnore
    @ApiModelProperty("0 0天;1 1天;2 2天;3 3天")
    private Integer accountingPeriod;


    /**
     * 挂单价状态（1: 可出价；2:待确认 3.失效）
     */
    @ExcelIgnore
    @ApiModelProperty("最高价 挂单价状态（1: 可出价；2:待确认 3.失效）")
    private Integer priceHighestStatus;

    /**
     * 挂单价状态（1: 可出价；2:待确认 3.失效）
     */
    @ExcelIgnore
    @ApiModelProperty("高价 挂单价状态（1: 可出价；2:待确认 3.失效）")
    private Integer priceHignStatus;

    /**
     * 挂单价状态（1: 可出价；2:待确认 3.失效）
     */
    @ExcelIgnore
    @ApiModelProperty("低价 挂单价状态（1: 可出价；2:待确认 3.失效）")
    private Integer priceLowStatus;

    /**
     * 挂单价状态（1: 可出价；2:待确认 3.失效）
     */
    @ExcelIgnore
    @ApiModelProperty("最低价 挂单价状态（1: 可出价；2:待确认 3.失效）")
    private Integer priceLowestStatus;

    @ExcelIgnore
    @ApiModelProperty("每次报价时间（单位分钟）")
    private Integer quotationInterval;

    @ExcelIgnore
    @ApiModelProperty("发货时效")
    private Integer deliveryTime;


    @ExcelIgnore
    @ApiModelProperty("最后一次抢单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastCompeteTime;


    @ExcelIgnore
    @ApiModelProperty("抢单价格")
    private BigDecimal tradePrice;

    /**
     * 抢单人姓名
     */
    @ExcelIgnore
    @ApiModelProperty("抢单人姓名")
    private String tradeUserName;

    /**
     * 抢单用户手机
     */
    @ExcelIgnore
    @ApiModelProperty("抢单用户手机")
    private String tradeUserPhone;


    /**
     * 抢单用户昵称
     */
    @ExcelIgnore
    @ApiModelProperty("抢单企业昵称")
    private String tradeNickName;

    /**
     * 抢单用户昵称
     */
    @ExcelIgnore
    @ApiModelProperty("抢单企业名称")
    private String tradeCompanyName;

    /**
     * 快递单号
     */
    @ExcelIgnore
    @ApiModelProperty("快递单号")
    private String trackingNumber;

    /**
     * 快递公司
     */
    @ExcelIgnore
    @ApiModelProperty("快递公司")
    private String trackingCompany;

    @ExcelIgnore
    @ApiModelProperty("处理申请,是否需要处理申请（ 1不需要 0 需要） ")
    private Integer handleApply;



}
