package com.ruoyi.analysis.convert;

import com.ruoyi.analysis.constant.AnalysisConstants;
import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.domain.AnalysisDailyMetric;
import com.ruoyi.analysis.model.bo.AnalysisDashboardBO;
import com.ruoyi.analysis.model.bo.AnalysisRebateBO;
import com.ruoyi.analysis.model.source.AnalysisMetricCalculation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 指标计算结果、持久化实体和看板对象之间的专用转换器。
 */
@Mapper(imports = AnalysisConstants.class)
public interface AnalysisMetricConvert {
    AnalysisMetricConvert INSTANCE = Mappers.getMapper(AnalysisMetricConvert.class);

    AnalysisDailyMetric toDomain(AnalysisMetricCalculation source);

    @Mapping(target = "goodsGrossMargin", ignore = true)
    AnalysisDashboardBO.MetricBO toMetricBO(AnalysisDailyMetric source);

    @Mapping(target = "goodsGrossMargin", ignore = true)
    AnalysisDashboardBO.MetricBO toMetricBO(AnalysisMetricCalculation source);

    @Mapping(target = "goodsGrossMargin", ignore = true)
    AnalysisDashboardBO.DimensionMetricBO toDimensionBO(AnalysisDailyMetric source);

    @Mapping(target = "calcStatus", expression = "java(value == null ? AnalysisConstants.STATUS_INCOMPLETE : AnalysisConstants.STATUS_COMPLETE)")
    @Mapping(target = "children", ignore = true)
    AnalysisDashboardBO.MetricNodeBO toMetricNode(String key, String name, BigDecimal value);

    AnalysisDashboardBO toDashboard(AnalysisDashboardBO.MetricBO summary,
                                    List<AnalysisDashboardBO.DimensionMetricBO> rows,
                                    List<AnalysisDashboardBO.MetricNodeBO> metricTree,
                                    int factCount, int incompleteCount, BigDecimal completenessRate);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configType", constant = "REBATE")
    @Mapping(target = "brand", source = "activity.brand")
    @Mapping(target = "goodsNo", source = "detail.goodsNo")
    @Mapping(target = "amount", source = "detail.amount")
    @Mapping(target = "coefficient", source = "detail.pointRate")
    @Mapping(target = "startDate", expression = "java(activity.getStartTime().toLocalDate())")
    @Mapping(target = "endDate", expression = "java(activity.getEndTime().toLocalDate())")
    AnalysisCostConfig toRebateConfig(AnalysisRebateBO activity, AnalysisRebateBO.DetailBO detail);

    @AfterMapping
    default void calculateMargin(AnalysisDailyMetric source,
                                 @MappingTarget AnalysisDashboardBO.MetricBO target) {
        target.setGoodsGrossMargin(margin(source.getGoodsGrossProfit(), source.getSalesRevenue()));
    }

    @AfterMapping
    default void calculateMargin(AnalysisMetricCalculation source,
                                 @MappingTarget AnalysisDashboardBO.MetricBO target) {
        target.setGoodsGrossMargin(margin(source.getGoodsGrossProfit(), source.getSalesRevenue()));
    }

    default BigDecimal margin(BigDecimal profit, BigDecimal revenue) {
        if (profit == null || revenue == null || revenue.signum() == 0) {
            return null;
        }
        return profit.multiply(BigDecimal.valueOf(100)).divide(revenue, 2, RoundingMode.HALF_UP);
    }
}
