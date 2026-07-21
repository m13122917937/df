package com.ruoyi.analysis.model.source;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 指标计算完成后、写入每日快照前的不可变结果。
 */
@Value
@Builder
public class AnalysisMetricCalculation {
    LocalDate metricDate;
    String subjectName;
    String platform;
    String shopName;
    String brand;
    String category;
    String goodsNo;
    String orderType;
    BigDecimal salesQuantity;
    BigDecimal goodsIncome;
    BigDecimal platformSubsidy;
    BigDecimal governmentSubsidy;
    BigDecimal salesRevenue;
    BigDecimal goodsCost;
    BigDecimal goodsIncentive;
    BigDecimal goodsGrossProfit;
    BigDecimal platformFee;
    BigDecimal logisticsFee;
    BigDecimal marketingFee;
    BigDecimal impairmentFee;
    BigDecimal penaltyFee;
    BigDecimal taxFee;
    BigDecimal fulfillmentGrossProfit;
    BigDecimal capitalCost;
    BigDecimal directLaborCost;
    BigDecimal directHeadcount;
    BigDecimal departmentDirectCost;
    BigDecimal otherAdjustment;
    BigDecimal departmentProfit;
    BigDecimal indirectLaborCost;
    BigDecimal indirectHeadcount;
    BigDecimal allocatedIndirectCost;
    BigDecimal operatingProfit;
    Integer factCount;
    Integer incompleteCount;
    String calcStatus;
}
