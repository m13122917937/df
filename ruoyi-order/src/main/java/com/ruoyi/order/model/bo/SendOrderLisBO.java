package com.ruoyi.order.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class SendOrderLisBO {
    // 订单基础信息
    private String orderCode;
    private String erpOrderId;
    private String originalOrderId;
    private String brand;
    private String category;
    private String platform;
    private String shopName;
    private String productName;
    private String skuName;
    private Integer quantity;

    // 订单状态与时间
    private Integer subStatus;
    private Date createTime;
    private Date sendTime;
    private Date shipmentsTime;
    private Date signedTime;
    private Date lastShippingTime;
    private Date lastCompeteTime;

    // 收货人信息
    private String province;
    private String city;
    private String addressee;
    private String phone;
    private String receivingAddress;

    // 挂单/价格处理相关
    private String handleApply;
    private Integer deliveryTime;
    private BigDecimal priceHighest;
    private Integer priceHighestStatus;
    private BigDecimal priceHign; // 注意：SQL中为hign，可能是拼写错误，保留原字段名
    private Integer priceHignStatus;
    private BigDecimal priceLow;
    private Integer priceLowStatus;
    private BigDecimal priceLowest;
    private Integer priceLowestStatus;

    // 交易与物流信息
    private String tradeCompanyName;
    private String tradeUserName;
    private String tradeUserPhone;
    private BigDecimal tradePrice;
    private String accountingPeriod;
    private String trackingNumber;
    private String tradeNickName;
    private String trackingCompany;

    // 订单样式
    private String orderStyle;



}
