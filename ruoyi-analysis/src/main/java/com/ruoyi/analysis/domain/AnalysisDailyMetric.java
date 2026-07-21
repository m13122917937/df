package com.ruoyi.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日经营指标快照，仅映射新表 ana_daily_metric。
 */
@Data
@TableName("ana_daily_metric")
public class AnalysisDailyMetric {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate metricDate;
    private String subjectName;
    private String platform;
    private String shopName;
    private String brand;
    private String category;
    private String goodsNo;
    private String orderType;
    private BigDecimal salesQuantity;
    private BigDecimal goodsIncome;
    private BigDecimal platformSubsidy;
    private BigDecimal governmentSubsidy;
    private BigDecimal salesRevenue;
    private BigDecimal goodsCost;
    private BigDecimal goodsIncentive;
    private BigDecimal goodsGrossProfit;
    private BigDecimal platformFee;
    private BigDecimal logisticsFee;
    private BigDecimal marketingFee;
    private BigDecimal impairmentFee;
    private BigDecimal penaltyFee;
    private BigDecimal taxFee;
    private BigDecimal fulfillmentGrossProfit;
    private BigDecimal capitalCost;
    private BigDecimal directLaborCost;
    private BigDecimal directHeadcount;
    private BigDecimal departmentDirectCost;
    private BigDecimal otherAdjustment;
    private BigDecimal departmentProfit;
    private BigDecimal indirectLaborCost;
    private BigDecimal indirectHeadcount;
    private BigDecimal allocatedIndirectCost;
    private BigDecimal operatingProfit;
    private Integer factCount;
    private Integer incompleteCount;
    private String calcStatus;
    private LocalDateTime updatedTime;
}
