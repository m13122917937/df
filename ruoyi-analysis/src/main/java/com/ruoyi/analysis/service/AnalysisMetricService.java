package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.analysis.constant.AnalysisConstants;
import com.ruoyi.analysis.config.AnalysisProperties;
import com.ruoyi.analysis.convert.AnalysisMetricConvert;
import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.domain.AnalysisDailyMetric;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.domain.AnalysisRebateActivity;
import com.ruoyi.analysis.domain.AnalysisRebateDetail;
import com.ruoyi.analysis.domain.AnalysisRefundFact;
import com.ruoyi.analysis.mapper.AnalysisCostConfigMapper;
import com.ruoyi.analysis.mapper.AnalysisDailyMetricMapper;
import com.ruoyi.analysis.mapper.AnalysisOrderFactMapper;
import com.ruoyi.analysis.mapper.AnalysisRebateActivityMapper;
import com.ruoyi.analysis.mapper.AnalysisRebateDetailMapper;
import com.ruoyi.analysis.mapper.AnalysisRefundFactMapper;
import com.ruoyi.analysis.model.bo.AnalysisDashboardBO;
import com.ruoyi.analysis.model.bo.AnalysisRebateBO;
import com.ruoyi.analysis.model.source.AnalysisMetricCalculation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.JacksonUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.function.Function;

/**
 * 经营指标计算与查询服务，只读写 ana_* 表。
 */
@Service
public class AnalysisMetricService extends ServiceImpl<AnalysisDailyMetricMapper, AnalysisDailyMetric> {
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Autowired
    private AnalysisOrderFactMapper factMapper;
    @Autowired
    private AnalysisRefundFactMapper refundMapper;
    @Autowired
    private AnalysisCostConfigMapper configMapper;
    @Autowired
    private AnalysisRebateActivityMapper rebateActivityMapper;
    @Autowired
    private AnalysisRebateDetailMapper rebateDetailMapper;
    @Autowired
    private AnalysisProperties properties;

    /**
     * 重新计算指定经营日期的快照。
     *
     * @param date 经营日期
     */
    @Transactional(rollbackFor = Exception.class)
    public void rebuildDate(LocalDate date) {
        if (date == null || date.isBefore(properties.getGoLiveLocalDate())) {
            throw new ServiceException("禁止重算模块上线日期之前的数据");
        }
        List<AnalysisOrderFact> facts = factMapper.selectByBusinessDate(date);
        List<AnalysisRefundFact> refunds = refundMapper.selectByRefundDate(date);
        List<AnalysisCostConfig> configs = loadConfigs(date);
        configs.addAll(loadRebateConfigs(date));
        Map<String, MetricAccumulator> groups = buildFactGroups(facts);
        applyRefunds(groups, refunds);
        applyConfigs(groups, configs, facts, date);
        baseMapper.deleteByMetricDate(date);
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
     * @param metrics 已按条件查询的指标快照
     * @return 看板数据
     */
    public AnalysisDashboardBO dashboard(List<AnalysisDailyMetric> metrics) {
        MetricAccumulator summary = new MetricAccumulator();
        for (AnalysisDailyMetric metric : metrics) {
            summary.add(metric);
        }
        AnalysisDashboardBO.MetricBO summaryBO = summary.toBO();
        List<AnalysisDashboardBO.DimensionMetricBO> rows = metrics.stream()
                .map(AnalysisMetricConvert.INSTANCE::toDimensionBO)
                .collect(Collectors.toList());
        return AnalysisMetricConvert.INSTANCE.toDashboard(summaryBO, rows, buildMetricTree(summaryBO),
                summary.factCount, summary.incompleteCount,
                calculateCompleteness(summary.factCount, summary.incompleteCount));
    }

    /**
     * 按经营主体汇总绩效数据。
     *
     * @param metrics 已过滤的每日经营指标
     * @return 绩效汇总看板
     */
    public AnalysisDashboardBO performanceRollup(List<AnalysisDailyMetric> metrics) {
        return dashboard(aggregate(metrics, metric -> safe(metric.getSubjectName())));
    }

    /**
     * 按品牌、品类、平台和店铺汇总产渠数据。
     *
     * @param metrics 已过滤的每日经营指标
     * @return 产渠分析看板
     */
    public AnalysisDashboardBO channelProduction(List<AnalysisDailyMetric> metrics) {
        return dashboard(aggregate(metrics, metric -> String.join("|", safe(metric.getBrand()),
                safe(metric.getCategory()), safe(metric.getPlatform()), safe(metric.getShopName()))));
    }

    /**
     * 按经营主体汇总人效相关收入、利润和人力成本。
     *
     * @param metrics 已过滤的每日经营指标
     * @return 人效分析看板
     */
    public AnalysisDashboardBO humanEfficiency(List<AnalysisDailyMetric> metrics) {
        return dashboard(aggregate(metrics, metric -> safe(metric.getSubjectName())));
    }

    private List<AnalysisCostConfig> loadConfigs(LocalDate date) {
        String month = date.toString().substring(0, 7);
        return configMapper.selectEffectiveByDate(date, month);
    }

    private List<AnalysisDailyMetric> aggregate(List<AnalysisDailyMetric> metrics,
                                                Function<AnalysisDailyMetric, String> keyFunction) {
        Map<String, MetricAccumulator> groups = new LinkedHashMap<>();
        for (AnalysisDailyMetric metric : metrics) {
            groups.computeIfAbsent(keyFunction.apply(metric), ignored -> new MetricAccumulator(metric)).add(metric);
        }
        return groups.values().stream().map(MetricAccumulator::toAggregateMetric)
                .collect(Collectors.toList());
    }

    private List<AnalysisCostConfig> loadRebateConfigs(LocalDate date) {
        List<AnalysisCostConfig> result = new ArrayList<>();
        for (AnalysisRebateActivity domain : rebateActivityMapper.selectAllOrdered()) {
            List<AnalysisRebateDetail> details = rebateDetailMapper.selectByActivityId(domain.getId());
            AnalysisRebateBO activity = AnalysisConvert.INSTANCE.toBO(domain, details);
            if (activity.getStartTime().toLocalDate().isAfter(date)
                    || activity.getEndTime().toLocalDate().isBefore(date)) {
                continue;
            }
            for (AnalysisRebateBO.DetailBO detail : activity.getDetails()) {
                result.add(AnalysisMetricConvert.INSTANCE.toRebateConfig(activity, detail));
            }
        }
        return result;
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
                refundMapper.updateById(refund);
                continue;
            }
            MetricAccumulator accumulator = groups.computeIfAbsent(dimensionKey(source),
                    unused -> new MetricAccumulator(source));
            accumulator.applyRefund(refund, source);
            refund.setMatchStatus(AnalysisConstants.STATUS_COMPLETE);
            refund.setExceptionReason(null);
            refundMapper.updateById(refund);
        }
    }

    private AnalysisOrderFact findRefundSource(AnalysisRefundFact refund) {
        return factMapper.selectRefundSource(refund.getOriginalOrderNo(), refund.getGoodsNo());
    }

    private void applyConfigs(Map<String, MetricAccumulator> groups, List<AnalysisCostConfig> configs,
                              List<AnalysisOrderFact> dailyFacts, LocalDate date) {
        for (AnalysisCostConfig config : configs) {
            Map<String, BigDecimal> dailyWeights = groupWeights(dailyFacts, config);
            if (dailyWeights.isEmpty() || (config.getAmount() == null && config.getCoefficient() == null)) {
                continue;
            }
            BigDecimal totalWeight = totalWeight(config, date, dailyWeights);
            String costScope = extraText(config, AnalysisConstants.EXTRA_COST_SCOPE);
            BigDecimal annualRate = extraDecimal(config, AnalysisConstants.EXTRA_ANNUAL_RATE);
            BigDecimal headcount = extraDecimal(config, AnalysisConstants.EXTRA_HEADCOUNT);
            for (Map.Entry<String, BigDecimal> entry : dailyWeights.entrySet()) {
                MetricAccumulator group = groups.get(entry.getKey());
                if (group == null) {
                    continue;
                }
                BigDecimal allocated = allocateConfigAmount(config.getAmount(), entry.getValue(), totalWeight);
                BigDecimal allocatedHeadcount = allocateConfigAmount(headcount, entry.getValue(), totalWeight);
                group.applyConfig(config, allocated, entry.getValue(), costScope, annualRate, allocatedHeadcount);
            }
        }
    }

    private Map<String, BigDecimal> groupWeights(List<AnalysisOrderFact> facts, AnalysisCostConfig config) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (AnalysisOrderFact fact : facts) {
            if (matchesConfig(fact, config)) {
                result.merge(dimensionKey(fact), factRevenue(fact), BigDecimal::add);
            }
        }
        return result;
    }

    private BigDecimal totalWeight(AnalysisCostConfig config, LocalDate date,
                                   Map<String, BigDecimal> dailyWeights) {
        LocalDate[] allocationRange = allocationRange(config, date);
        if (allocationRange == null) {
            return sumWeights(dailyWeights.values());
        }
        List<AnalysisOrderFact> allocationFacts = factMapper.selectByBusinessDateRange(
                allocationRange[0], allocationRange[1]);
        BigDecimal allocationWeight = allocationFacts.stream().filter(fact -> matchesConfig(fact, config))
                .map(this::factRevenue).reduce(BigDecimal.ZERO, BigDecimal::add);
        return allocationWeight.signum() == 0 ? sumWeights(dailyWeights.values()) : allocationWeight;
    }

    private BigDecimal allocateConfigAmount(BigDecimal amount, BigDecimal weight, BigDecimal totalWeight) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        if (totalWeight.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(weight).divide(totalWeight, 4, RoundingMode.HALF_UP);
    }

    private LocalDate[] allocationRange(AnalysisCostConfig config, LocalDate date) {
        if (config.getBusinessDate() != null) {
            return null;
        }
        if (config.getMonthValue() != null && date.toString().startsWith(config.getMonthValue() + "-")) {
            return new LocalDate[]{date.withDayOfMonth(1), date.withDayOfMonth(date.lengthOfMonth())};
        }
        if (config.getStartDate() != null && config.getEndDate() != null) {
            return new LocalDate[]{config.getStartDate(), config.getEndDate()};
        }
        return null;
    }

    private BigDecimal sumWeights(Iterable<BigDecimal> weights) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal weight : weights) {
            total = total.add(weight);
        }
        return total;
    }

    private boolean matchesConfig(AnalysisOrderFact fact, AnalysisCostConfig config) {
        return matchesValue(config.getPlatform(), fact.getPlatform())
                && matchesValue(config.getShopName(), fact.getShopName())
                && matchesValue(config.getOriginalOrderNo(), fact.getOriginalOrderNo())
                && matchesValue(config.getGoodsNo(), fact.getGoodsNo())
                && matchesValue(config.getBrand(), fact.getBrand())
                && matchesValue(config.getCategory(), fact.getCategory());
    }

    private boolean matchesValue(String configured, String actual) {
        return configured == null || configured.isEmpty() || Objects.equals(configured, actual);
    }

    private BigDecimal factRevenue(AnalysisOrderFact fact) {
        return zero(fact.getPaymentAmount()).add(zero(fact.getPlatformSubsidy()))
                .add(zero(fact.getGovernmentSubsidy()));
    }

    private String extraText(AnalysisCostConfig config, String field) {
        JsonNode node = extraNode(config);
        return node == null || !node.hasNonNull(field) ? null : node.get(field).asText();
    }

    private BigDecimal extraDecimal(AnalysisCostConfig config, String field) {
        JsonNode node = extraNode(config);
        if (node == null || !node.hasNonNull(field)) {
            return null;
        }
        try {
            return node.get(field).decimalValue();
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private JsonNode extraNode(AnalysisCostConfig config) {
        if (config.getExtraData() == null || config.getExtraData().trim().isEmpty()) {
            return null;
        }
        try {
            return JacksonUtil.readTree(config.getExtraData());
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private String dimensionKey(AnalysisOrderFact fact) {
        return String.join("|", safe(fact.getSubjectName()), safe(fact.getPlatform()), safe(fact.getShopName()),
                safe(fact.getBrand()), safe(fact.getCategory()), safe(fact.getGoodsNo()), safe(fact.getOrderType()));
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
        return AnalysisMetricConvert.INSTANCE.toMetricNode(key, name, value);
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
        private BigDecimal directHeadcount = BigDecimal.ZERO;
        private BigDecimal departmentDirectCost = BigDecimal.ZERO;
        private BigDecimal otherAdjustment = BigDecimal.ZERO;
        private BigDecimal indirectLaborCost = BigDecimal.ZERO;
        private BigDecimal indirectHeadcount = BigDecimal.ZERO;
        private BigDecimal allocatedIndirectCost = BigDecimal.ZERO;
        private int factCount;
        private int incompleteCount;
        private LocalDate metricDate;

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
            metricDate = fact.getBusinessDate();
        }

        private MetricAccumulator(AnalysisDailyMetric metric) {
            subjectName = safeValue(metric.getSubjectName());
            platform = safeValue(metric.getPlatform());
            shopName = safeValue(metric.getShopName());
            brand = safeValue(metric.getBrand());
            category = safeValue(metric.getCategory());
            goodsNo = safeValue(metric.getGoodsNo());
            orderType = safeValue(metric.getOrderType(), "NORMAL");
            metricDate = metric.getMetricDate();
        }

        private void add(AnalysisOrderFact fact) {
            quantity = quantity.add(zero(fact.getQuantity()));
            goodsIncome = goodsIncome.add(zero(fact.getPaymentAmount()));
            platformSubsidy = platformSubsidy.add(zero(fact.getPlatformSubsidy()));
            governmentSubsidy = governmentSubsidy.add(zero(fact.getGovernmentSubsidy()));
            logisticsFee = logisticsFee.add(zero(fact.getOrderExpense()));
            goodsIncentive = goodsIncentive.add(zero(fact.getGoodsIncentive()));
            factCount++;
            if (fact.getGoodsCost() == null) {
                incompleteCount++;
            } else {
                goodsCost = goodsCost.add(fact.getGoodsCost());
            }
        }

        private void add(AnalysisDailyMetric metric) {
            if (metricDate == null) {
                metricDate = metric.getMetricDate();
            }
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
            directHeadcount = directHeadcount.add(zero(metric.getDirectHeadcount()));
            departmentDirectCost = departmentDirectCost.add(zero(metric.getDepartmentDirectCost()));
            otherAdjustment = otherAdjustment.add(zero(metric.getOtherAdjustment()));
            indirectLaborCost = indirectLaborCost.add(zero(metric.getIndirectLaborCost()));
            indirectHeadcount = indirectHeadcount.add(zero(metric.getIndirectHeadcount()));
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

        private void applyConfig(AnalysisCostConfig config, BigDecimal amount, BigDecimal coefficientBase,
                                 String costScope, BigDecimal annualRate, BigDecimal headcount) {
            String type = config.getConfigType();
            if ("REBATE".equals(type)) {
                goodsIncentive = goodsIncentive.add(amount);
                if (config.getCoefficient() != null) {
                    goodsIncentive = goodsIncentive.add(coefficientBase.multiply(config.getCoefficient()));
                }
            } else if ("FIXED_COEFFICIENT".equals(type) && config.getCoefficient() != null) {
                platformFee = platformFee.add(amount).add(coefficientBase.multiply(config.getCoefficient()));
            } else if ("FIXED_COEFFICIENT".equals(type)) {
                platformFee = platformFee.add(amount);
            } else if ("PLATFORM_FEE".equals(type)) {
                platformFee = platformFee.add(amount);
            } else if ("LOGISTICS".equals(type)) {
                logisticsFee = logisticsFee.add(amount);
            } else if ("CASHBACK".equals(type) || "PROMOTION".equals(type)) {
                marketingFee = marketingFee.add(amount);
            } else if ("PENALTY".equals(type)) {
                penaltyFee = penaltyFee.add(amount);
            } else if ("IMPAIRMENT".equals(type)) {
                impairmentFee = impairmentFee.add(amount);
            } else if ("TAX".equals(type)) {
                taxFee = taxFee.add(amount);
            } else if ("OTHER_ADJUSTMENT".equals(type)) {
                otherAdjustment = otherAdjustment.add(amount);
            } else if ("COLLECTION_DAYS".equals(type)) {
                capitalCost = capitalCost.add(amount).add(collectionCost(coefficientBase,
                        config.getCoefficient(), annualRate));
            } else if ("INTERNAL_COST".equals(type)) {
                applyInternalCost(amount, headcount, costScope);
            } else if ("WAREHOUSE_COST".equals(type)) {
                applyWarehouseCost(amount, costScope);
            }
        }

        private BigDecimal collectionCost(BigDecimal revenue, BigDecimal collectionDays, BigDecimal annualRate) {
            if (collectionDays == null || annualRate == null) {
                return BigDecimal.ZERO;
            }
            return revenue.multiply(annualRate).multiply(collectionDays)
                    .divide(BigDecimal.valueOf(365), 4, RoundingMode.HALF_UP);
        }

        private void applyInternalCost(BigDecimal amount, BigDecimal headcount, String costScope) {
            if (AnalysisConstants.COST_SCOPE_INDIRECT.equals(costScope)) {
                indirectLaborCost = indirectLaborCost.add(amount);
                indirectHeadcount = indirectHeadcount.add(headcount);
            } else if (AnalysisConstants.COST_SCOPE_DEPARTMENT.equals(costScope)) {
                departmentDirectCost = departmentDirectCost.add(amount);
            } else {
                directLaborCost = directLaborCost.add(amount);
                directHeadcount = directHeadcount.add(headcount);
            }
        }

        private void applyWarehouseCost(BigDecimal amount, String costScope) {
            if (AnalysisConstants.COST_SCOPE_DIRECT.equals(costScope)) {
                departmentDirectCost = departmentDirectCost.add(amount);
            } else {
                allocatedIndirectCost = allocatedIndirectCost.add(amount);
            }
        }

        private AnalysisDailyMetric toMetric(LocalDate date) {
            return AnalysisMetricConvert.INSTANCE.toDomain(calculate(date));
        }

        private AnalysisDailyMetric toAggregateMetric() {
            return toMetric(metricDate);
        }

        private AnalysisMetricCalculation calculate(LocalDate date) {
            BigDecimal revenue = goodsIncome.add(platformSubsidy).add(governmentSubsidy);
            BigDecimal gross = incompleteCount == 0 ? revenue.subtract(goodsCost).add(goodsIncentive) : null;
            BigDecimal fulfillment = gross == null ? null : gross.subtract(platformFee).subtract(logisticsFee)
                    .subtract(marketingFee).subtract(impairmentFee).subtract(penaltyFee).subtract(taxFee);
            BigDecimal department = fulfillment == null ? null : fulfillment.subtract(capitalCost)
                    .subtract(directLaborCost).subtract(departmentDirectCost).add(otherAdjustment);
            BigDecimal operating = department == null ? null : department.subtract(indirectLaborCost)
                    .subtract(allocatedIndirectCost);
            return AnalysisMetricCalculation.builder()
                    .metricDate(date).subjectName(subjectName).platform(platform).shopName(shopName)
                    .brand(brand).category(category).goodsNo(goodsNo).orderType(orderType)
                    .salesQuantity(quantity).goodsIncome(goodsIncome).platformSubsidy(platformSubsidy)
                    .governmentSubsidy(governmentSubsidy).salesRevenue(revenue)
                    .goodsCost(incompleteCount == 0 ? goodsCost : null).goodsIncentive(goodsIncentive)
                    .goodsGrossProfit(gross).platformFee(platformFee).logisticsFee(logisticsFee)
                    .marketingFee(marketingFee).impairmentFee(impairmentFee).penaltyFee(penaltyFee)
                    .taxFee(taxFee).fulfillmentGrossProfit(fulfillment).capitalCost(capitalCost)
                    .directLaborCost(directLaborCost).directHeadcount(directHeadcount)
                    .departmentDirectCost(departmentDirectCost)
                    .otherAdjustment(otherAdjustment).departmentProfit(department)
                    .indirectLaborCost(indirectLaborCost).indirectHeadcount(indirectHeadcount)
                    .allocatedIndirectCost(allocatedIndirectCost)
                    .operatingProfit(operating).factCount(factCount).incompleteCount(incompleteCount)
                    .calcStatus(incompleteCount == 0 ? AnalysisConstants.STATUS_COMPLETE
                            : AnalysisConstants.STATUS_INCOMPLETE)
                    .build();
        }

        private AnalysisDashboardBO.MetricBO toBO() {
            return AnalysisMetricConvert.INSTANCE.toMetricBO(calculate(null));
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
