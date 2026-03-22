package com.ruoyi.web.vo.index;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data

@Accessors(chain = true)
public class IndexSkuListVO {


    private String orderCode;


    private String platform;


    private Long hangingOrderId;


    private String productName;


    private String skuName;


    private Long quantity;


    private Long province;


    private Long city;


    private String provinceName;


    private String cityName;


    private Integer accountingPeriod;


    private Integer deliveryTime;



    private BigDecimal priceHighest;




    private BigDecimal priceHign;



    private BigDecimal priceLow;



    private BigDecimal priceLowest;



    private Integer priceHighestStatus;



    private Integer priceHignStatus;



    private Integer priceLowStatus;



    private Integer priceLowestStatus;


    private Integer quotationInterval;


    private List<String> otherRequire;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastCompeteTime;



    private BigDecimal tradePrice;


    private Integer orderStyle;



}
