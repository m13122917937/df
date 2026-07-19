package com.ruoyi.analysis.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.ruoyi.analysis.config.AnalysisProperties;
import com.ruoyi.analysis.constant.AnalysisConstants;
import com.ruoyi.analysis.convert.AnalysisSyncConvert;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.domain.AnalysisRefundFact;
import com.ruoyi.analysis.domain.AnalysisSyncLog;
import com.ruoyi.analysis.mapper.AnalysisOrderFactMapper;
import com.ruoyi.analysis.mapper.AnalysisRefundFactMapper;
import com.ruoyi.analysis.mapper.AnalysisSyncLogMapper;
import com.ruoyi.analysis.model.source.AnalysisOrderFactSource;
import com.ruoyi.analysis.model.source.AnalysisRefundFactSource;
import com.ruoyi.analysis.model.source.AnalysisSyncLogSource;
import com.ruoyi.analysis.model.source.AnalysisSyncResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.order.OrderQueryParam;
import com.ruoyi.jky.param.refund.RefundQueryParam;
import com.ruoyi.jky.rep.order.OrderQueryRep;
import com.ruoyi.jky.rep.refund.RefundQueryRep;
import com.ruoyi.jky.util.JkyResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * 吉客云经营数据同步服务，只向 ana_* 新表写数据。
 */
@Service
public class AnalysisSyncService {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
    private static final String ORDER_FIELDS = "tradeNo,orderNo,onlineTradeNo,sourceTradeNo,tradeStatus,tradeTime,"
            + "consignTime,completeTime,billDate,shopId,shopName,shopTypeCode,companyName,warehouseId,warehouseName,"
            + "expense.expenseFee,expense.expenseItemName,totalFee,payment,discountFee,"
            + "goodsDetail.goodsId,goodsDetail.goodsNo,goodsDetail.outerId,goodsDetail.goodsName,goodsDetail.specName,"
            + "goodsDetail.sellCount,goodsDetail.needProcessCount,goodsDetail.sellPrice,goodsDetail.sellTotal,"
            + "goodsDetail.isGift,goodsDetail.assessmentCost,goodsDetail.goodsPlatDiscountFee,"
            + "goodsDetail.shareOrderDiscountFee,goodsDetail.shareOrderPlatDiscountFee,scrollId";

    @Autowired
    private JkyTemplate jkyTemplate;
    @Autowired
    private AnalysisProperties properties;
    @Autowired
    private AnalysisOrderFactMapper factMapper;
    @Autowired
    private AnalysisRefundFactMapper refundMapper;
    @Autowired
    private AnalysisSyncLogMapper logMapper;

    /**
     * 同步指定日期的发货、修改订单和退款并重算快照。
     *
     * @param date 目标自然日
     * @return 同步结果及受影响日期
     */
    public AnalysisSyncResult syncDate(LocalDate date) {
        validateDate(date);
        AnalysisSyncLog log = startLog(date);
        SyncCounter counter = new SyncCounter();
        try {
            Map<String, OrderQueryRep.OrderTradeRep> trades = loadOrders(date);
            counter.readCount += trades.size();
            Set<LocalDate> affectedDates = persistTrades(trades.values(), counter);
            affectedDates.addAll(persistRefunds(loadRefunds(date), counter));
            affectedDates.add(date);
            log = completeLog(log, counter, AnalysisConstants.STATUS_COMPLETE, null);
            return new AnalysisSyncResult(com.ruoyi.analysis.convert.AnalysisConvert.INSTANCE.toBO(log), affectedDates);
        } catch (Exception exception) {
            completeLog(log, counter, AnalysisConstants.STATUS_FAILED, exception.getMessage());
            throw exception instanceof ServiceException ? (ServiceException) exception
                    : new ServiceException("经营分析同步失败：" + exception.getMessage());
        }
    }

    /**
     * 同步前一自然日。
     *
     * @return 同步结果及受影响日期
     */
    public AnalysisSyncResult syncYesterday() {
        return syncDate(LocalDate.now().minusDays(1));
    }

    private Map<String, OrderQueryRep.OrderTradeRep> loadOrders(LocalDate date) {
        Map<String, OrderQueryRep.OrderTradeRep> trades = new LinkedHashMap<>();
        loadOrderWindow(date, true, trades);
        loadOrderWindow(date, false, trades);
        return trades;
    }

    private void loadOrderWindow(LocalDate date, boolean consignWindow,
                                 Map<String, OrderQueryRep.OrderTradeRep> result) {
        String scrollId = "";
        for (int page = 0; page < 10000; page++) {
            OrderQueryParam param = buildOrderParam(date, consignWindow, scrollId);
            JkyResponse<OrderQueryRep> response = jkyTemplate.queryOrders(param);
            OrderQueryRep data = requireData(response, "订单");
            if (data == null || CollectionUtil.isEmpty(data.getTrades())) {
                return;
            }
            for (OrderQueryRep.OrderTradeRep trade : data.getTrades()) {
                if (trade != null && StrUtil.isNotBlank(trade.getTradeNo())) {
                    result.put(trade.getTradeNo(), trade);
                }
            }
            if (StrUtil.isBlank(data.getScrollId()) || Objects.equals(scrollId, data.getScrollId())) {
                return;
            }
            scrollId = data.getScrollId();
        }
        throw new ServiceException("吉客云订单滚动查询超过安全页数");
    }

    private OrderQueryParam buildOrderParam(LocalDate date, boolean consignWindow, String scrollId) {
        OrderQueryParam param = new OrderQueryParam();
        param.setFields(ORDER_FIELDS);
        param.setScrollId(scrollId);
        param.setPageSize(100);
        param.setIsReturnPddData(1);
        param.setIsPddQuery(0);
        param.setIsDelete("0");
        String start = date.atStartOfDay().format(DATE_TIME);
        String end = date.atTime(23, 59, 59).format(DATE_TIME);
        if (consignWindow) {
            param.setStartConsignTime(start);
            param.setEndConsignTime(end);
        } else {
            param.setStartModified(start);
            param.setEndModified(end);
        }
        return param;
    }

    private Set<LocalDate> persistTrades(Iterable<OrderQueryRep.OrderTradeRep> trades, SyncCounter counter) {
        Set<LocalDate> affectedDates = new LinkedHashSet<>();
        for (OrderQueryRep.OrderTradeRep trade : trades) {
            LocalDateTime consignTime = parseDateTime(trade.getConsignTime());
            if (consignTime == null || consignTime.toLocalDate().isBefore(properties.getGoLiveLocalDate())) {
                counter.skipCount++;
                continue;
            }
            List<AnalysisOrderFact> facts = buildFacts(trade, consignTime);
            for (AnalysisOrderFact fact : facts) {
                affectedDates.add(fact.getBusinessDate());
                if (upsertFact(fact)) {
                    counter.insertCount++;
                } else {
                    counter.updateCount++;
                }
            }
        }
        return affectedDates;
    }

    private List<AnalysisOrderFact> buildFacts(OrderQueryRep.OrderTradeRep trade, LocalDateTime consignTime) {
        List<OrderQueryRep.OrderGoodsDetailRep> goods = validGoods(trade.getGoodsDetail());
        List<AnalysisOrderFact> facts = new ArrayList<>();
        BigDecimal totalWeight = totalWeight(goods);
        BigDecimal payment = firstNonNull(trade.getPayment(), trade.getTotalFee(), totalWeight);
        BigDecimal expense = totalExpense(trade.getExpense());
        BigDecimal allocatedPayment = BigDecimal.ZERO;
        BigDecimal allocatedExpense = BigDecimal.ZERO;
        for (int index = 0; index < goods.size(); index++) {
            boolean last = index == goods.size() - 1;
            BigDecimal weight = lineWeight(goods.get(index));
            BigDecimal linePayment = last ? payment.subtract(allocatedPayment)
                    : allocate(payment, weight, totalWeight);
            BigDecimal lineExpense = last ? expense.subtract(allocatedExpense)
                    : allocate(expense, weight, totalWeight);
            facts.add(buildFact(trade, goods.get(index), index, consignTime, linePayment, lineExpense));
            allocatedPayment = allocatedPayment.add(linePayment);
            allocatedExpense = allocatedExpense.add(lineExpense);
        }
        return facts;
    }

    private AnalysisOrderFact buildFact(OrderQueryRep.OrderTradeRep trade,
                                        OrderQueryRep.OrderGoodsDetailRep goods, int index,
                                        LocalDateTime consignTime, BigDecimal payment, BigDecimal expense) {
        String lineKey = goodsLineKey(goods, index);
        BigDecimal quantity = firstNonNull(goods.getNeedProcessCount(), goods.getSellCount(), BigDecimal.ZERO);
        BigDecimal goodsAmount = lineWeight(goods);
        BigDecimal cost = goods.getAssessmentCost() == null ? null : goods.getAssessmentCost().multiply(quantity);
        BigDecimal subsidy = zero(goods.getGoodsPlatDiscountFee()).add(zero(goods.getShareOrderPlatDiscountFee()));
        AnalysisOrderFactSource source = AnalysisOrderFactSource.builder()
                .factKey(trade.getTradeNo() + ":" + lineKey).jkyTradeNo(trade.getTradeNo())
                .jkyOrderNo(trade.getOrderNo()).originalOrderNo(firstNotBlank(trade.getOnlineTradeNo(),
                        trade.getSourceTradeNo(), trade.getOrderNo())).goodsLineKey(lineKey)
                .businessDate(consignTime.toLocalDate()).tradeTime(parseDateTime(trade.getTradeTime()))
                .consignTime(consignTime).completeTime(parseDateTime(trade.getCompleteTime()))
                .sourceModifiedTime(parseDateTime(trade.getBillDate())).subjectName(trade.getCompanyName())
                .platform(StrUtil.blankToDefault(trade.getShopTypeCode(), "吉客云"))
                .shopId(stringValue(trade.getShopId())).shopName(trade.getShopName())
                .warehouseId(stringValue(trade.getWarehouseId())).warehouseName(trade.getWarehouseName())
                .supplierName(trade.getCompanyName()).goodsId(goods.getGoodsId())
                .goodsNo(firstNotBlank(goods.getGoodsNo(), goods.getOuterId())).goodsName(goods.getGoodsName())
                .specName(goods.getSpecName()).quantity(quantity)
                .unitPrice(firstNonNull(goods.getSellPrice(), BigDecimal.ZERO)).goodsAmount(goodsAmount)
                .paymentAmount(payment).discountAmount(zero(goods.getShareOrderDiscountFee()))
                .platformSubsidy(subsidy).governmentSubsidy(BigDecimal.ZERO).orderExpense(expense)
                .assessmentCost(goods.getAssessmentCost()).goodsCost(cost).goodsIncentive(BigDecimal.ZERO)
                .goodsGrossProfit(cost == null ? null : payment.add(subsidy).subtract(cost))
                .orderStatus(String.valueOf(trade.getTradeStatus()))
                .orderType(Objects.equals(goods.getIsGift(), 1) ? "GIFT" : "NORMAL")
                .giftFlag(zeroInt(goods.getIsGift()))
                .calcStatus(cost == null ? AnalysisConstants.STATUS_INCOMPLETE : AnalysisConstants.STATUS_COMPLETE)
                .missingReason(cost == null ? "吉客云未返回商品评估成本" : null)
                .rawHash(SecureUtil.sha256(JacksonUtil.toJson(trade) + "#" + index)).build();
        return AnalysisSyncConvert.INSTANCE.toDomain(source);
    }

    private List<RefundQueryRep.TradeAfterOnlineWrapper> loadRefunds(LocalDate date) {
        List<RefundQueryRep.TradeAfterOnlineWrapper> result = new ArrayList<>();
        for (int page = 1; page <= 10000; page++) {
            RefundQueryParam param = buildRefundParam(date, page);
            RefundQueryRep data = requireData(jkyTemplate.listRefunds(param), "退款");
            if (data == null || CollectionUtil.isEmpty(data.getTradeAfterOnlineDtoArr())) {
                return result;
            }
            result.addAll(data.getTradeAfterOnlineDtoArr());
            if (data.getTradeAfterOnlineDtoArr().size() < 100) {
                return result;
            }
        }
        throw new ServiceException("吉客云退款分页查询超过安全页数");
    }

    private RefundQueryParam buildRefundParam(LocalDate date, int page) {
        RefundQueryParam param = new RefundQueryParam();
        RefundQueryParam.PageInfo pageInfo = new RefundQueryParam.PageInfo();
        pageInfo.setPageIndex(page);
        pageInfo.setPageSize(100);
        param.setPageInfo(pageInfo);
        param.setIsQueryCount(true);
        param.setHasQueryHistory(1);
        param.setGmtModifiedBegin(date.atStartOfDay().format(DATE_TIME));
        param.setGmtModifiedEnd(date.atTime(23, 59, 59).format(DATE_TIME));
        return param;
    }

    private Set<LocalDate> persistRefunds(List<RefundQueryRep.TradeAfterOnlineWrapper> wrappers, SyncCounter counter) {
        Set<LocalDate> affectedDates = new LinkedHashSet<>();
        for (RefundQueryRep.TradeAfterOnlineWrapper wrapper : wrappers) {
            if (wrapper == null || wrapper.getTradeAfterOnlineDTO() == null) {
                counter.skipCount++;
                continue;
            }
            List<AnalysisRefundFact> facts = buildRefundFacts(wrapper);
            counter.readCount += facts.size();
            for (AnalysisRefundFact fact : facts) {
                affectedDates.add(fact.getRefundDate());
                if (upsertRefund(fact)) {
                    counter.insertCount++;
                } else {
                    counter.updateCount++;
                }
            }
        }
        return affectedDates;
    }

    private List<AnalysisRefundFact> buildRefundFacts(RefundQueryRep.TradeAfterOnlineWrapper wrapper) {
        RefundQueryRep.RefundTradeRep trade = wrapper.getTradeAfterOnlineDTO();
        LocalDateTime successTime = parseDateTime(firstNotBlank(trade.getRefundSuccessTime(), trade.getGmtModified()));
        if (successTime == null || successTime.toLocalDate().isBefore(properties.getGoLiveLocalDate())) {
            return new ArrayList<>();
        }
        List<RefundQueryRep.RefundGoodsRep> goodsList = wrapper.getTradeAfterOnlineGoodsDTOList();
        if (CollectionUtil.isEmpty(goodsList)) {
            goodsList = new ArrayList<>();
            goodsList.add(new RefundQueryRep.RefundGoodsRep());
        }
        return allocateRefunds(trade, goodsList, successTime);
    }

    private List<AnalysisRefundFact> allocateRefunds(RefundQueryRep.RefundTradeRep trade,
                                                     List<RefundQueryRep.RefundGoodsRep> goods,
                                                     LocalDateTime successTime) {
        List<AnalysisRefundFact> result = new ArrayList<>();
        BigDecimal total = firstNonNull(trade.getRefundAmount(), BigDecimal.ZERO);
        BigDecimal totalWeight = goods.stream().map(item -> zero(item.getSellTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal allocated = BigDecimal.ZERO;
        for (int index = 0; index < goods.size(); index++) {
            boolean last = index == goods.size() - 1;
            BigDecimal amount = last ? total.subtract(allocated)
                    : allocate(total, zero(goods.get(index).getSellTotal()), totalWeight);
            result.add(buildRefundFact(trade, goods.get(index), index, successTime, amount));
            allocated = allocated.add(amount);
        }
        return result;
    }

    private AnalysisRefundFact buildRefundFact(RefundQueryRep.RefundTradeRep trade,
                                               RefundQueryRep.RefundGoodsRep goods, int index,
                                               LocalDateTime successTime, BigDecimal amount) {
        String lineKey = firstNotBlank(goods.getGoodsId(), goods.getOuterId(), goods.getOuterSkuId(), String.valueOf(index));
        AnalysisRefundFactSource source = AnalysisRefundFactSource.builder()
                .refundKey(trade.getRefundNo() + ":" + lineKey).refundNo(trade.getRefundNo())
                .originalOrderNo(trade.getPlatOrderNo()).goodsLineKey(lineKey)
                .goodsNo(firstNotBlank(goods.getGoodsNo(), goods.getOuterId()))
                .goodsName(firstNotBlank(goods.getGoodsName(), goods.getTradeGoodsName()))
                .refundSuccessTime(successTime).refundDate(successTime.toLocalDate()).refundAmount(amount)
                .platformRefundAmount(zero(trade.getPlatRefundAmount())).refundQuantity(zero(goods.getSellCount()))
                .hasGoodsReturn(zeroInt(trade.getHasGoodsReturn())).reversedCost(BigDecimal.ZERO)
                .matchStatus(AnalysisConstants.STATUS_PENDING)
                .rawHash(SecureUtil.sha256(JacksonUtil.toJson(trade) + JacksonUtil.toJson(goods))).build();
        return AnalysisSyncConvert.INSTANCE.toDomain(source);
    }

    private AnalysisSyncLog startLog(LocalDate date) {
        AnalysisSyncLogSource source = AnalysisSyncLogSource.builder().syncType(AnalysisConstants.SYNC_DAILY)
                .windowStart(date.atStartOfDay()).windowEnd(date.atTime(23, 59, 59))
                .status(AnalysisConstants.STATUS_RUNNING).readCount(0).insertCount(0).updateCount(0)
                .skipCount(0).startedTime(LocalDateTime.now()).build();
        AnalysisSyncLog log = AnalysisSyncConvert.INSTANCE.toDomain(source);
        logMapper.insert(log);
        return log;
    }

    private AnalysisSyncLog completeLog(AnalysisSyncLog log, SyncCounter counter, String status, String error) {
        AnalysisSyncLogSource source = AnalysisSyncLogSource.builder().id(log.getId())
                .syncType(log.getSyncType()).windowStart(log.getWindowStart()).windowEnd(log.getWindowEnd())
                .status(status).readCount(counter.readCount).insertCount(counter.insertCount)
                .updateCount(counter.updateCount).skipCount(counter.skipCount)
                .errorMessage(StrUtil.sub(error, 0, 4000)).startedTime(log.getStartedTime())
                .finishedTime(LocalDateTime.now()).build();
        AnalysisSyncLog completed = AnalysisSyncConvert.INSTANCE.toDomain(source);
        logMapper.updateById(completed);
        return completed;
    }

    private boolean upsertFact(AnalysisOrderFact fact) {
        AnalysisOrderFact existing = factMapper.selectByFactKey(fact.getFactKey());
        if (existing == null) {
            factMapper.insert(fact);
            return true;
        }
        fact.setId(existing.getId());
        factMapper.updateById(fact);
        return false;
    }

    private boolean upsertRefund(AnalysisRefundFact fact) {
        AnalysisRefundFact existing = refundMapper.selectByRefundKey(fact.getRefundKey());
        if (existing == null) {
            refundMapper.insert(fact);
            return true;
        }
        fact.setId(existing.getId());
        refundMapper.updateById(fact);
        return false;
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new ServiceException("同步日期不能为空");
        }
        if (date.isBefore(properties.getGoLiveLocalDate())) {
            throw new ServiceException("禁止同步模块上线日期之前的数据");
        }
        if (!date.isBefore(LocalDate.now())) {
            throw new ServiceException("只能同步已经结束的自然日");
        }
    }

    private <T> T requireData(JkyResponse<T> response, String name) {
        if (!JkyResponseUtil.isSuccess(response)) {
            throw new ServiceException("吉客云" + name + "查询失败：" + (response == null ? "无响应" : response.getMsg()));
        }
        return JkyResponseUtil.getData(response);
    }

    private List<OrderQueryRep.OrderGoodsDetailRep> validGoods(List<OrderQueryRep.OrderGoodsDetailRep> goods) {
        List<OrderQueryRep.OrderGoodsDetailRep> result = new ArrayList<>();
        if (goods != null) {
            for (OrderQueryRep.OrderGoodsDetailRep item : goods) {
                if (item != null) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    private BigDecimal totalWeight(List<OrderQueryRep.OrderGoodsDetailRep> goods) {
        BigDecimal result = goods.stream().map(this::lineWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
        return result.signum() == 0 ? BigDecimal.valueOf(Math.max(goods.size(), 1)) : result;
    }

    private BigDecimal lineWeight(OrderQueryRep.OrderGoodsDetailRep goods) {
        if (goods.getSellTotal() != null && goods.getSellTotal().signum() != 0) {
            return goods.getSellTotal();
        }
        BigDecimal quantity = firstNonNull(goods.getNeedProcessCount(), goods.getSellCount(), BigDecimal.ONE);
        return zero(goods.getSellPrice()).multiply(quantity);
    }

    private BigDecimal totalExpense(List<OrderQueryRep.OrderExpenseRep> expenses) {
        if (expenses == null) {
            return BigDecimal.ZERO;
        }
        return expenses.stream().map(item -> zero(item.getExpenseFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal allocate(BigDecimal total, BigDecimal weight, BigDecimal totalWeight) {
        if (total == null || totalWeight == null || totalWeight.signum() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal safeWeight = weight == null || weight.signum() == 0 ? BigDecimal.ONE : weight;
        return total.multiply(safeWeight).divide(totalWeight, 4, RoundingMode.HALF_UP);
    }

    private String goodsLineKey(OrderQueryRep.OrderGoodsDetailRep goods, int index) {
        if (StrUtil.isNotBlank(goods.getGoodsId())) {
            return goods.getGoodsId();
        }
        return SecureUtil.md5(firstNotBlank(goods.getGoodsNo(), goods.getOuterId(), "UNKNOWN")
                + "|" + StrUtil.nullToEmpty(goods.getSpecName()) + "|" + index);
    }

    private LocalDateTime parseDateTime(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DATE_TIME);
        } catch (Exception ignored) {
            return DateUtil.toLocalDateTime(DateUtil.parse(value));
        }
    }

    private String firstNotBlank(String... values) {
        for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private BigDecimal firstNonNull(BigDecimal... values) {
        for (BigDecimal value : values) {
            if (value != null) {
                return value;
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal zero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private int zeroInt(Integer value) {
        return value == null ? 0 : value;
    }

    private static final class SyncCounter {
        private int readCount;
        private int insertCount;
        private int updateCount;
        private int skipCount;
    }
}
