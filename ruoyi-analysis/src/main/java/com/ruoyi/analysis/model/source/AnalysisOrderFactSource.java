package com.ruoyi.analysis.model.source;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 吉客云订单商品行转换为经营事实前的计算结果。
 */
@Value
@Builder
public class AnalysisOrderFactSource {
    String factKey;
    String jkyTradeNo;
    String jkyOrderNo;
    String originalOrderNo;
    String goodsLineKey;
    LocalDate businessDate;
    LocalDateTime tradeTime;
    LocalDateTime consignTime;
    LocalDateTime completeTime;
    LocalDateTime sourceModifiedTime;
    String subjectName;
    String platform;
    String shopId;
    String shopName;
    String warehouseId;
    String warehouseName;
    String supplierName;
    String goodsId;
    String goodsNo;
    String goodsName;
    String specName;
    BigDecimal quantity;
    BigDecimal unitPrice;
    BigDecimal goodsAmount;
    BigDecimal paymentAmount;
    BigDecimal discountAmount;
    BigDecimal platformSubsidy;
    BigDecimal governmentSubsidy;
    BigDecimal orderExpense;
    BigDecimal assessmentCost;
    BigDecimal goodsCost;
    BigDecimal goodsIncentive;
    BigDecimal goodsGrossProfit;
    String orderStatus;
    String orderType;
    Integer giftFlag;
    String calcStatus;
    String missingReason;
    String rawHash;
}
