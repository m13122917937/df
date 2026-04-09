package com.ruoyi.order.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CompanyOrderDTO {

    private String orderCode;

    private String erpOrderId;

    private String originalOrderId;

    private String tradeCompanyName;

    private Integer status;
    // o.order_code
    private String tradeUserName;          // oto.trade_user_name
    private String tradeUserPhone;          // oto.trade_user_name

    private Date createTime;      // oto.create_time

    private Date sendTime;

    private Date shipmentsTime;   // o.shipments_time

    private Date signedTime;      // o.signed_time

    private String brand;                  // o.brand
    private String category;               // o.category
    private String productName;            // o.product_name
    private String skuName;                // o.sku_name
    private Integer quantity;              // o.quantity

    private String province;               // o.province
    private String city;                   // o.city
    private String addressee;              // o.addressee
    private String phone;                  // o.phone
    private String receivingAddress;       // o.receiving_address
    private BigDecimal tradePrice;         // oto.trade_price
    private String accountingPeriod;       // oto.accounting_period
    private String trackingNumber;         // oto.tracking_number
    private String trackingCompany;        // oto.tracking_company

    private Date lastCompeteTime;


    private Integer handleApply;

    private Integer subStatus;

    private Integer orderStyle;
    private Integer warehouseQuantity;
    private Integer deliveryCode;

    private String remark;

    private String createBy;

    private String payerName;
}
