package com.ruoyi.web.vo.analysis;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 经营分析看板响应对象。
 */
@Data
public class AnalysisDashboardVO {
    private MetricVO summary = new MetricVO();
    private List<DimensionMetricVO> rows = new ArrayList<>();
    private List<MetricNodeVO> metricTree = new ArrayList<>();
    private int factCount;
    private int incompleteCount;
    private BigDecimal completenessRate;

    /**
     * 指标金额响应对象。
     */
    @Data
    public static class MetricVO {
        private BigDecimal salesQuantity;
        private BigDecimal goodsIncome;
        private BigDecimal platformSubsidy;
        private BigDecimal governmentSubsidy;
        private BigDecimal salesRevenue;
        private BigDecimal goodsCost;
        private BigDecimal goodsIncentive;
        private BigDecimal goodsGrossProfit;
        private BigDecimal goodsGrossMargin;
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
        private String calcStatus;
    }

    /**
     * 多维指标响应对象。
     */
    @Data
    public static class DimensionMetricVO extends MetricVO {
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
     * 指标树节点响应对象。
     */
    @Data
    public static class MetricNodeVO {
        private String key;
        private String name;
        private BigDecimal value;
        private String calcStatus;
        private List<MetricNodeVO> children = new ArrayList<>();
    }
}
