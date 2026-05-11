package com.ruoyi.web.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data

public class AllOrderVO {


    private String orderCode;


    private String brand;

    private String category;

    private String productName;

    private String skuName;

    private String skuCode;

    private Integer quantity;


    private String tradeUserName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date tradeTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date signedTime;



    private Long province;

    private String provinceName;

    private Long city;

    private String cityName;

    private String addressee;

    private String phone;

    private String receivingAddress;


    private BigDecimal tradePrice;


    private String accountingPeriod;


    private String trackingNumber;

    private String trackingCompany;


    private Integer revokeType;


    private Integer status;

    private Integer orderType;

    private String subStatus;
}
