package com.ruoyi.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 经营分析订单商品行事实，仅映射新表 ana_order_fact。
 */
@Data
@TableName("ana_order_fact")
public class AnalysisOrderFact {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String factKey;
    private String jkyTradeNo;
    private String jkyOrderNo;
    private String originalOrderNo;
    private String goodsLineKey;
    private LocalDate businessDate;
    private LocalDateTime tradeTime;
    private LocalDateTime consignTime;
    private LocalDateTime completeTime;
    private LocalDateTime sourceModifiedTime;
    private String subjectName;
    private String platform;
    private String shopId;
    private String shopName;
    private String warehouseId;
    private String warehouseName;
    private String supplierName;
    private String goodsId;
    private String goodsNo;
    private String goodsName;
    private String specName;
    private String brand;
    private String category;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal goodsAmount;
    private BigDecimal paymentAmount;
    private BigDecimal discountAmount;
    private BigDecimal platformSubsidy;
    private BigDecimal governmentSubsidy;
    private BigDecimal orderExpense;
    private BigDecimal assessmentCost;
    private BigDecimal goodsCost;
    private BigDecimal goodsIncentive;
    private BigDecimal goodsGrossProfit;
    private BigDecimal fulfillmentGrossProfit;
    private BigDecimal departmentProfit;
    private BigDecimal operatingProfit;
    private String orderType;
    private String orderStatus;
    private Integer giftFlag;
    private String calcStatus;
    private String missingReason;
    private String rawHash;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
