package com.ruoyi.web.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data

public class WarehousingOrderVO {


    private String orderCode;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    private String tradeUserName;


    private String createBy;


    private String brand;


    private String category;


    private String productName;


    private String skuName;


    private Integer quantity;


    private Integer warehouseQuantity;


    private BigDecimal tradePrice;


    private String tradeCompanyName;


    private Integer deliveryCode;



    private String remark;

}
