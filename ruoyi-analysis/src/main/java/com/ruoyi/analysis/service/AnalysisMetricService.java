package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.constant.AnalysisConstants;
import com.ruoyi.analysis.config.AnalysisProperties;
import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.domain.AnalysisDailyMetric;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.domain.AnalysisRefundFact;
import com.ruoyi.analysis.mapper.AnalysisDailyMetricMapper;
import com.ruoyi.analysis.model.bo.AnalysisDashboardBO;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 经营指标计算与查询服务，只读写 ana_* 表。
 */
@Service
public class AnalysisMetricService extends ServiceImpl<AnalysisDailyMetricMapper, AnalysisDailyMetric> {
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Autowired
    private AnalysisFactService factService;
    @Autowired
    private AnalysisRefundService refundService;
    @Autowired
    private AnalysisCostConfigService configService;
    @Autowired
    private AnalysisProperties properties;

    /**
     * 重新计算指定经营日期的快照。
     *
     * @param date 经营日期
     */
    @Transactional(rollbackFor = Exception.class)
    public void rebuildDate(LocalDate date) {
        if (date == null || date.isBefore(properties.getGoLiveDate())) {
            throw new ServiceException("禁止重算模块上线日期之前的数据");
        }
        List<AnalysisOrderFact> facts = factService.listByBusinessDate(date);
        List<AnalysisRefundFact> refunds = refundService.list(new LambdaQueryWrapper<AnalysisRefundFact>()
                .eq(AnalysisRefundFact::getRefundDate, date));
        List<AnalysisCostConfig> configs = loadConfigs(date);
        Map<String, MetricAccumulator> groups = buildFactGroups(facts);
        applyRefunds(groups, refunds);
        applyConfigs(groups, configs);
        remove(new LambdaQueryWrapper<AnalysisDailyMetric>()
                .eq(AnalysisDailyMetric::getMetricDate, date));
        List<AnalysisDailyMetric> metrics = groups.values().stream()
                .map(value -> value.toMetric(date))
                .collect(Collectors.toList());
        if (!metrics.isEmpty()) {
            saveBatch(metrics);
        }
    }

    /**
     * 查询经营看板数据。
     *
     * @param query 查询条件
     * @return 看板数据
     */
    public AnalysisDashboardBO dashboard(AnalysisQuery query) {
        List<AnalysisDailyMetric> metrics = listMetrics(query);
        AnalysisDashboardBO result = new AnalysisDashboardBO();
        MetricAccumulator summary = new MetricAccumulator();
        for (AnalysisDailyMetric metric : metrics) {
            summary.add(metric);
            result.getRows().add(toRow(metric));
        }
        result.setSummary(summary.toBO());
        result.setFactCount(summary.factCount);
        result.setIncompleteCount(summary.incompleteCount);
        result.setCompletenessRate(calculateCompleteness(summary.factCount, summary.incompleteCount));
        result.setMetricTree(buildMetricTree(result.getSummary()));
        return result;
    }

    private List<AnalysisDailyMetric> listMetrics(AnalysisQuery query) {
        LambdaQueryWrapper<AnalysisDailyMetric> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(query.getStartDate() != null, AnalysisDailyMetric::getMetricDate, query.getStartDate())
                .le(query.getEndDate() != null, AnalysisDailyMetric::getMetricDate, query.getEndDate())
                .eq(query.getSubjectName() != null, AnalysisDailyMetric::getSubjectName, query.getSubjectName())
                .eq(query.getPlatform() != null, AnalysisDailyMetric::getPlatform, query.getPlatform())
                .eq(query.getShopName() != null, AnalysisDailyMetric::getShopName, query.getShopName())
                .eq(query.getBrand() != null, AnalysisDailyMetric::getBrand, query.getBrand())
                .eq(query.getCategory() != null, AnalysisDailyMetric::getCategory, query.getCategory())
                .eq(query.getGoodsNo() != null, AnalysisDailyMetric::getGoodsNo, query.getGoodsNo())
                .eq(query.getOrderType() != null, AnalysisDailyMetric::getOrderType, query.getOrderType())
                .eq(query.getCalcStatus() != null, AnalysisDailyMetric::getCalcStatus, query.getCalcStatus())
                .orderByAsc(AnalysisDailyMetric::getMetricDate);
        return list(wrapper);
    }

    private List<AnalysisCostConfig> loadConfigs(LocalDate date) {
        String month = date.toString().substring(0, 7);
        return configService.list(new LambdaQueryWrapper<AnalysisCostConfig>()
                .and(wrapper -> wrapper.eq(AnalysisCostConfig::getBusinessDate, date)
                        .or().eq(AnalysisCostConfig::getMonthValue, month)
                        .or().and(value -> value.le(AnalysisCostConfig::getStartDate, date)
                                .ge(AnalysisCostConfig::getEndDate, date))));
    }

    private Map<String, MetricAccumulator> buildFactGroups(List<AnalysisOrderFact> facts) {
        Map<String, MetricAccumulator> groups = new LinkedHashMap<>();
        for (AnalysisOrderFact fact : facts) {
            String key = dimensionKey(fact);
            groups.computeIfAbsent(key, unused -> new MetricAccumulator(fact)).add(fact);
        }
        return groups;
    }

    private void applyRefunds(Map<String, MetricAccumulator> groups, List<AnalysisRefundFact> refunds) {
        for (AnalysisRefundFact refund : refunds) {
            AnalysisOrderFact source = findRefundSource(refund);
            if (source == null) {
                refund.setMatchStatus(AnalysisConstants.STATUS_INCOMPLETE);
                refund.setExceptionReason("未匹配到上线后的订单商品行");
                refundService.updateById(refund);
                continue;
            }
            MetricAccumulator accumulator = groups.computeIfAbsent(dimensionKey(source),
                    unused -> new MetricAccumulator(source));
            accumulator.applyRefund(refund, source);
            refund.setMatchStatus(AnalysisConstants.STATUS_COMPLETE);
            refund.setExceptionReason(null);
            refundService.updateById(refund);
        }
    }

    private AnalysisOrderFact findRefundSource(AnalysisRefundFact refund) {
        return factService.getOne(new LambdaQueryWrapper<AnalysisOrderFact>()
                .eq(AnalysisOrderFact::getOriginalOrderNo, refund.getOriginalOrderNo())
                .eq(refund.getGoodsNo() != null, AnalysisOrderFact::getGoodsNo, refund.getGoodsNo())
                .orderByDesc(AnalysisOrderFact::getBusinessDate)
                .last("limit 1"));
    }

    private void applyConfigs(Map<String, MetricAccumulator> groups, List<AnalysisCostConfig> configs) {
        for (AnalysisCostConfig config : configs) {
            List<MetricAccumulator> matched = groups.values().stream()
                    .filter(group -> group.matches(config))
                    .collect(Collectors.toList());
            if (matched.isEmpty() || config.getAmount() == null) {
                continue;
            }
            BigDecimal allocated = config.getAmount().divide(BigDecimal.valueOf(matched.size()), 4, RoundingMode.HALF_UP);
            for (MetricAccumulator group : matched) {
                group.applyConfig(config.getConfigType(), allocated);
            }
        }
    }

    private String dimensionKey(AnalysisOrderFact fact) {
        return String.join("|", safe(fact.getSubjectName()), safe(fact.getPlatform()), safe(fact.getShopName()),
                safe(fact.getBrand()), safe(fact.getCategory()), safe(fact.getGoodsNo()), safe(fact.getOrderType()));
    }

    private AnalysisDashboardBO.DimensionMetricBO toRow(AnalysisDailyMetric metric) {
        AnalysisDashboardBO.DimensionMetricBO row = new AnalysisDashboardBO.DimensionMetricBO();
        MetricAccumulator.copyMetric(row, metric);
        row.setMetricDate(metric.getMetricDate());
        row.setSubjectName(metric.getSubjectName());
        row.setPlatform(metric.getPlatform());
        row.setShopName(metric.getShopName());
        row.setBrand(metric.getBrand());
        row.setCategory(metric.getCategory());
        row.setGoodsNo(metric.getGoodsNo());
        row.setOrderType(metric.getOrderType());
        row.setFactCount(metric.getFactCount());
        row.setIncompleteCount(metric.getIncompleteCount());
        return row;
    }

    private List<AnalysisDashboardBO.MetricNodeBO> buildMetricTree(AnalysisDashboardBO.MetricBO metric) {
        List<AnalysisDashboardBO.MetricNodeBO> nodes = new ArrayList<>();
        nodes.add(node("salesRevenue", "销售收入", metric.getSalesRevenue()));
        nodes.add(node("goodsCost", "商品成本", metric.getGoodsCost()));
        nodes.add(node("goodsIncentive", "商品激励", metric.getGoodsIncentive()));
        nodes.add(node("goodsGrossProfit", "商品毛利", metric.getGoodsGrossProfit()));
        nodes.add(node("fulfillmentGrossProfit", "履约毛利", metric.getFulfillmentGrossProfit()));
        nodes.add(node("departmentProfit", "部门利润", metric.getDepartmentProfit()));
        nodes.add(node("operatingProfit", "经营利润", metric.getOperatingProfit()));
        return nodes;
    }

    private AnalysisDashboardBO.MetricNodeBO node(String key, String name, BigDecimal value) {
        AnalysisDashboardBO.MetricNodeBO node = new AnalysisDashboardBO.MetricNodeBO();
        node.setKey(key);
        node.setName(name);
        node.setValue(value);
        node.setCalcStatus(value == null ? AnalysisConstants.STATUS_INCOMPLETE : AnalysisConstants.STATUS_COMPLETE);
        return node;
    }

    private BigDecimal calculateCompleteness(int total, int incomplete) {
        if (total == 0) {
            return HUNDRED;
        }
        return BigDecimal.valueOf(total - incomplete).multiply(HUNDRED)
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private static BigDecimal zero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static final class MetricAccumulator {
        private String subjectName = "";
        private String platform = "";
        private String shopName = "";
        private String brand = "";
        private String category = "";
        private String goodsNo = "";
        private String orderType = "NORMAL";
        private BigDecimal quantity = BigDecimal.ZERO;
        private BigDecimal goodsIncome = BigDecimal.ZERO;
        private BigDecimal platformSubsidy = BigDecimal.ZERO;
        private BigDecimal governmentSubsidy = BigDecimal.ZERO;
        private BigDecimal goodsCost = BigDecimal.ZERO;
        private BigDecimal goodsIncentive = BigDecimal.ZERO;
        private BigDecimal platformFee = BigDecimal.ZERO;
        private BigDecimal logisticsFee = BigDecimal.ZERO;
        private BigDecimal marketingFee = BigDecimal.ZERO;
        private BigDecimal impairmentFee = BigDecimal.ZERO;
        private BigDecimal penaltyFee = BigDecimal.ZERO;
        private BigDecimal taxFee = BigDecimal.ZERO;
        private BigDecimal capitalCost = BigDecimal.ZERO;
        private BigDecimal directLaborCost = BigDecimal.ZERO;
        private BigDecimal departmentDirectCost = BigDecimal.ZERO;
        private BigDecimal otherAdjustment = BigDecimal.ZERO;
        private BigDecimal indirectLaborCost = BigDecimal.ZERO;
        private BigDecimal allocatedIndirectCost = BigDecimal.ZERO;
        private int factCount;
        private int incompleteCount;

        private MetricAccumulator() {
        }

        private MetricAccumulator(AnalysisOrderFact fact) {
            subjectName = safeValue(fact.getSubjectName());
            platform = safeValue(fact.getPlatform());
            shopName = safeValue(fact.getShopName());
            brand = safeValue(fact.getBrand());
            category = safeValue(fact.getCategory());
            goodsNo = safeValue(fact.getGoodsNo());
            orderType = safeValue(fact.getOrderType(), "NORMAL");
        }

        private void add(AnalysisOrderFact fact) {
            quantity = quantity.add(zero(fact.getQuantity()));
            goodsIncome = goodsIncome.add(zero(fact.getPaymentAmount()));
            platformSubsidy = platformSubsidy.add(zero(fact.getPlatformSubsidy()));
            governmentSubsidy = governmentSubsidy.add(zero(fact.getGovernmentSubsidy()));
            goodsIncentive = goodsIncentive.add(zero(fact.getGoodsIncentive()));
            factCount++;
            if (fact.getGoodsCost() == null) {
                incompleteCount++;
            } else {
                goodsCost = goodsCost.add(fact.getGoodsCost());
            }
        }

        private void add(AnalysisDailyMetric metric) {
            quantity = quantity.add(zero(metric.getSalesQuantity()));
            goodsIncome = goodsIncome.add(zero(metric.getGoodsIncome()));
            platformSubsidy = platformSubsidy.add(zero(metric.getPlatformSubsidy()));
            governmentSubsidy = governmentSubsidy.add(zero(metric.getGovernmentSubsidy()));
            goodsCost = goodsCost.add(zero(metric.getGoodsCost()));
            goodsIncentive = goodsIncentive.add(zero(metric.getGoodsIncentive()));
            platformFee = platformFee.add(zero(metric.getPlatformFee()));
            logisticsFee = logisticsFee.add(zero(metric.getLogisticsFee()));
            marketingFee = marketingFee.add(zero(metric.getMarketingFee()));
            impairmentFee = impairmentFee.add(zero(metric.getImpairmentFee()));
            penaltyFee = penaltyFee.add(zero(metric.getPenaltyFee()));
            taxFee = taxFee.add(zero(metric.getTaxFee()));
            capitalCost = capitalCost.add(zero(metric.getCapitalCost()));
            directLaborCost = directLaborCost.add(zero(metric.getDirectLaborCost()));
            departmentDirectCost = departmentDirectCost.add(zero(metric.getDepartmentDirectCost()));
            otherAdjustment = otherAdjustment.add(zero(metric.getOtherAdjustment()));
            indirectLaborCost = indirectLaborCost.add(zero(metric.getIndirectLaborCost()));
            allocatedIndirectCost = allocatedIndirectCost.add(zero(metric.getAllocatedIndirectCost()));
            factCount += zeroInt(metric.getFactCount());
            incompleteCount += zeroInt(metric.getIncompleteCount());
        }

        private void applyRefund(AnalysisRefundFact refund, AnalysisOrderFact source) {
            goodsIncome = goodsIncome.subtract(zero(refund.getRefundAmount()));
            if (Objects.equals(refund.getHasGoodsReturn(), 1)) {
                BigDecimal refundedQuantity = zero(refund.getRefundQuantity());
                quantity = quantity.subtract(refundedQuantity);
                BigDecimal reversed = calculateReversedCost(source, refundedQuantity);
                goodsCost = goodsCost.subtract(reversed);
                refund.setReversedCost(reversed);
            }
        }

        private BigDecimal calculateReversedCost(AnalysisOrderFact source, BigDecimal refundQuantity) {
            if (source.getAssessmentCost() != null) {
                return source.getAssessmentCost().multiply(refundQuantity);
            }
            if (source.getGoodsCost() != null && zero(source.getQuantity()).signum() > 0) {
                return source.getGoodsCost().multiply(refundQuantity)
                        .divide(source.getQuantity(), 4, RoundingMode.HALF_UP);
            }
            incompleteCount++;
            return BigDecimal.ZERO;
        }

        private boolean matches(AnalysisCostConfig config) {
            return matchesValue(config.getPlatform(), platform)
                    && matchesValue(config.getShopName(), shopName)
                    && matchesValue(config.getGoodsNo(), goodsNo)
                    && matchesValue(config.getBrand(), brand)
                    && matchesValue(config.getCategory(), category);
        }

        private void applyConfig(String type, BigDecimal amount) {
            if ("CASHBACK".equals(type) || "PROMOTION".equals(type)) {
                marketingFee = marketingFee.add(amount);
            } else if ("PENALTY".equals(type)) {
                penaltyFee = penaltyFee.add(amount);
            } else if ("MARGIN".equals(type) || "COLLECTION_DAYS".equals(type)) {
                capitalCost = capitalCost.add(amount);
            } else if ("INTERNAL_COST".equals(type)) {
                directLaborCost = directLaborCost.add(amount);
            } else if ("WAREHOUSE_COST".equals(type)) {
                allocatedIndirectCost = allocatedIndirectCost.add(amount);
            }
        }

        private AnalysisDailyMetric toMetric(LocalDate date) {
            AnalysisDailyMetric metric = new AnalysisDailyMetric();
            metric.setMetricDate(date);
            metric.setSubjectName(subjectName);
            metric.setPlatform(platform);
            metric.setShopName(shopName);
            metric.setBrand(brand);
            metric.setCategory(category);
            metric.setGoodsNo(goodsNo);
            metric.setOrderType(orderType);
            fillMetric(metric);
            return metric;
        }

        private void fillMetric(AnalysisDailyMetric metric) {
            BigDecimal revenue = goodsIncome.add(platformSubsidy).add(governmentSubsidy);
            BigDecimal gross = incompleteCount == 0 ? revenue.subtract(goodsCost).add(goodsIncentive) : null;
            BigDecimal fulfillment = gross == null ? null : gross.subtract(platformFee).subtract(logisticsFee)
                    .subtract(marketingFee).subtract(impairmentFee).subtract(penaltyFee).subtract(taxFee);
            BigDecimal department = fulfillment == null ? null : fulfillment.subtract(capitalCost)
                    .subtract(directLaborCost).subtract(departmentDirectCost).add(otherAdjustment);
            BigDecimal operating = department == null ? null : department.subtract(indirectLaborCost)
                    .subtract(allocatedIndirectCost);
            setAmounts(metric, revenue, gross, fulfillment, department, operating);
        }

        private void setAmounts(AnalysisDailyMetric metric, BigDecimal revenue, BigDecimal gross,
                                BigDecimal fulfillment, BigDecimal department, BigDecimal operating) {
            metric.setSalesQuantity(quantity);
            metric.setGoodsIncome(goodsIncome);
            metric.setPlatformSubsidy(platformSubsidy);
            metric.setGovernmentSubsidy(governmentSubsidy);
            metric.setSalesRevenue(revenue);
            metric.setGoodsCost(incompleteCount == 0 ? goodsCost : null);
            metric.setGoodsIncentive(goodsIncentive);
            metric.setGoodsGrossProfit(gross);
            metric.setPlatformFee(platformFee);
            metric.setLogisticsFee(logisticsFee);
            metric.setMarketingFee(marketingFee);
            metric.setImpairmentFee(impairmentFee);
            metric.setPenaltyFee(penaltyFee);
            metric.setTaxFee(taxFee);
            metric.setFulfillmentGrossProfit(fulfillment);
            metric.setCapitalCost(capitalCost);
            metric.setDirectLaborCost(directLaborCost);
            metric.setDepartmentDirectCost(departmentDirectCost);
            metric.setOtherAdjustment(otherAdjustment);
            metric.setDepartmentProfit(department);
            metric.setIndirectLaborCost(indirectLaborCost);
            metric.setAllocatedIndirectCost(allocatedIndirectCost);
            metric.setOperatingProfit(operating);
            metric.setFactCount(factCount);
            metric.setIncompleteCount(incompleteCount);
            metric.setCalcStatus(incompleteCount == 0 ? AnalysisConstants.STATUS_COMPLETE : AnalysisConstants.STATUS_INCOMPLETE);
        }

        private AnalysisDashboardBO.MetricBO toBO() {
            AnalysisDailyMetric metric = new AnalysisDailyMetric();
            fillMetric(metric);
            AnalysisDashboardBO.MetricBO result = new AnalysisDashboardBO.MetricBO();
            copyMetric(result, metric);
            return result;
        }

        private static void copyMetric(AnalysisDashboardBO.MetricBO target, AnalysisDailyMetric source) {
            target.setSalesQuantity(source.getSalesQuantity());
            target.setGoodsIncome(source.getGoodsIncome());
            target.setPlatformSubsidy(source.getPlatformSubsidy());
            target.setGovernmentSubsidy(source.getGovernmentSubsidy());
            target.setSalesRevenue(source.getSalesRevenue());
            target.setGoodsCost(source.getGoodsCost());
            target.setGoodsIncentive(source.getGoodsIncentive());
            target.setGoodsGrossProfit(source.getGoodsGrossProfit());
            target.setGoodsGrossMargin(margin(source.getGoodsGrossProfit(), source.getSalesRevenue()));
            target.setPlatformFee(source.getPlatformFee());
            target.setLogisticsFee(source.getLogisticsFee());
            target.setMarketingFee(source.getMarketingFee());
            target.setImpairmentFee(source.getImpairmentFee());
            target.setPenaltyFee(source.getPenaltyFee());
            target.setTaxFee(source.getTaxFee());
            target.setFulfillmentGrossProfit(source.getFulfillmentGrossProfit());
            target.setCapitalCost(source.getCapitalCost());
            target.setDirectLaborCost(source.getDirectLaborCost());
            target.setDepartmentDirectCost(source.getDepartmentDirectCost());
            target.setOtherAdjustment(source.getOtherAdjustment());
            target.setDepartmentProfit(source.getDepartmentProfit());
            target.setIndirectLaborCost(source.getIndirectLaborCost());
            target.setAllocatedIndirectCost(source.getAllocatedIndirectCost());
            target.setOperatingProfit(source.getOperatingProfit());
            target.setCalcStatus(source.getCalcStatus());
        }

        private static BigDecimal margin(BigDecimal profit, BigDecimal revenue) {
            if (profit == null || revenue == null || revenue.signum() == 0) {
                return null;
            }
            return profit.multiply(HUNDRED).divide(revenue, 2, RoundingMode.HALF_UP);
        }

        private static boolean matchesValue(String configured, String actual) {
            return configured == null || configured.isEmpty() || Objects.equals(configured, actual);
        }

        private static String safeValue(String value) {
            return safeValue(value, "");
        }

        private static String safeValue(String value, String defaultValue) {
            return value == null ? defaultValue : value;
        }

        private static int zeroInt(Integer value) {
            return value == null ? 0 : value;
        }
    }
}
