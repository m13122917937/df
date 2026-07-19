package com.ruoyi.web.vo.analysis;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 经营订单明细响应对象。
 */
@Data
public class AnalysisOrderFactVO {
    private Long id;
    private String jkyTradeNo;
    private String jkyOrderNo;
    private String originalOrderNo;
    private LocalDate businessDate;
    private LocalDateTime tradeTime;
    private LocalDateTime consignTime;
    private String subjectName;
    private String platform;
    private String shopName;
    private String warehouseName;
    private String supplierName;
    private String goodsNo;
    private String goodsName;
    private String specName;
    private String brand;
    private String category;
    private BigDecimal quantity;
    private BigDecimal goodsAmount;
    private BigDecimal paymentAmount;
    private BigDecimal goodsCost;
    private BigDecimal goodsGrossProfit;
    private BigDecimal fulfillmentGrossProfit;
    private BigDecimal departmentProfit;
    private BigDecimal operatingProfit;
    private String orderType;
    private String calcStatus;
    private String missingReason;
}
