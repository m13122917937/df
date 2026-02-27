package com.ruoyi.bill.model.bo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentOperationsBO {

    private Long supplierId;

    private String supplierName;

    private BigDecimal billingAmount;

    private Long billCount;

    private Long abnormalCount;

    private BigDecimal notPayMoney;

    private BigDecimal havePayMoney;
}
