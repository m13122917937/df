package com.ruoyi.bill.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentOperationsDTO {

    private Long supplierId;

    private String supplierName;

    private Long payCompanyId;

    private String payCompanyName;

    private BigDecimal billingAmount;

    private Long billCount;

    private Long abnormalCount;

    private BigDecimal notPayMoney;

    private BigDecimal havePayMoney;
}
