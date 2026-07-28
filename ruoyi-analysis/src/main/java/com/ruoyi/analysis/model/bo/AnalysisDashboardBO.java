package com.ruoyi.analysis.model.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 经营分析看板业务对象。
 */
@Data
public class AnalysisDashboardBO {
    private MetricBO summary = new MetricBO();
    private List<DimensionMetricBO> rows = new ArrayList<>();
    private List<MetricNodeBO> metricTree = new ArrayList<>();
    private int factCount;
    private int incompleteCount;
    private BigDecimal completenessRate;

    /**
     * 指标金额集合。
     */
    @Data
    public static class MetricBO {
        private BigDecimal salesQuantity = BigDecimal.ZERO;
        private BigDecimal goodsIncome = BigDecimal.ZERO;
        private BigDecimal platformSubsidy = BigDecimal.ZERO;
        private BigDecimal governmentSubsidy = BigDecimal.ZERO;
        private BigDecimal salesRevenue = BigDecimal.ZERO;
        private BigDecimal goodsCost;
        private BigDecimal goodsIncentive = BigDecimal.ZERO;
        private BigDecimal goodsGrossProfit;
        private BigDecimal goodsGrossMargin;
        private BigDecimal platformFee = BigDecimal.ZERO;
        private BigDecimal logisticsFee = BigDecimal.ZERO;
        private BigDecimal marketingFee = BigDecimal.ZERO;
        private BigDecimal impairmentFee = BigDecimal.ZERO;
        private BigDecimal penaltyFee = BigDecimal.ZERO;
        private BigDecimal taxFee = BigDecimal.ZERO;
        private BigDecimal fulfillmentGrossProfit;
        private BigDecimal capitalCost = BigDecimal.ZERO;
        private BigDecimal directLaborCost = BigDecimal.ZERO;
        private BigDecimal departmentDirectCost = BigDecimal.ZERO;
        private BigDecimal otherAdjustment = BigDecimal.ZERO;
        private BigDecimal departmentProfit;
        private BigDecimal indirectLaborCost = BigDecimal.ZERO;
        private BigDecimal allocatedIndirectCost = BigDecimal.ZERO;
        private BigDecimal operatingProfit;
        private String calcStatus = "COMPLETE";
    }

    /**
     * 按日期或业务维度汇总的指标行。
     */
    @Data
    public static class DimensionMetricBO extends MetricBO {
        private LocalDate metricDate;
        private String subjectName;
        private String platform;
        private String shopName;
        private String brand;
        private String category;
        private String goodsNo;
        private String orderType;
        private Integer factCount;
        private Integer incompleteCount;
    }

    /**
     * 多级指标树节点。
     */
    @Data
    public static class MetricNodeBO {
        private String key;
        private String name;
        private BigDecimal value;
        private String calcStatus;
        private List<MetricNodeBO> children = new ArrayList<>();
    }
}
