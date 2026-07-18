package com.ruoyi.analysis.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.ruoyi.analysis.config.AnalysisProperties;
import com.ruoyi.analysis.constant.AnalysisConstants;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.domain.AnalysisRefundFact;
import com.ruoyi.analysis.domain.AnalysisSyncLog;
import com.ruoyi.analysis.model.bo.AnalysisSyncBO;
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
    private AnalysisFactService factService;
    @Autowired
    private AnalysisRefundService refundService;
    @Autowired
    private AnalysisSyncLogService logService;
    @Autowired
    private AnalysisMetricService metricService;

    /**
     * 同步指定日期的发货、修改订单和退款并重算快照。
     *
     * @param date 目标自然日
     * @return 同步结果
     */
    public AnalysisSyncBO syncDate(LocalDate date) {
        validateDate(date);
        AnalysisSyncLog log = startLog(date);
        SyncCounter counter = new SyncCounter();
        try {
            Map<String, OrderQueryRep.OrderTradeRep> trades = loadOrders(date);
            counter.readCount += trades.size();
            persistTrades(trades.values(), counter);
            persistRefunds(loadRefunds(date), counter);
            metricService.rebuildDate(date);
            completeLog(log, counter, AnalysisConstants.STATUS_COMPLETE, null);
        } catch (Exception exception) {
            completeLog(log, counter, AnalysisConstants.STATUS_FAILED, exception.getMessage());
            throw exception instanceof ServiceException ? (ServiceException) exception
                    : new ServiceException("经营分析同步失败：" + exception.getMessage());
        }
        return com.ruoyi.analysis.convert.AnalysisConvert.INSTANCE.toBO(log);
    }

    /**
     * 同步前一自然日。
     *
     * @return 同步结果
     */
    public AnalysisSyncBO syncYesterday() {
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

    private void persistTrades(Iterable<OrderQueryRep.OrderTradeRep> trades, SyncCounter counter) {
        for (OrderQueryRep.OrderTradeRep trade : trades) {
            LocalDateTime consignTime = parseDateTime(trade.getConsignTime());
            if (consignTime == null || consignTime.toLocalDate().isBefore(properties.getGoLiveDate())) {
                counter.skipCount++;
                continue;
            }
            List<AnalysisOrderFact> facts = buildFacts(trade, consignTime);
            for (AnalysisOrderFact fact : facts) {
                if (factService.upsert(fact)) {
                    counter.insertCount++;
                } else {
                    counter.updateCount++;
                }
            }
        }
    }

    private List<AnalysisOrderFact> buildFacts(OrderQueryRep.OrderTradeRep trade, LocalDateTime consignTime) {
        List<OrderQueryRep.OrderGoodsDetailRep> goods = nonGiftGoods(trade.getGoodsDetail());
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
        AnalysisOrderFact fact = new AnalysisOrderFact();
        String lineKey = goodsLineKey(goods, index);
        fact.setFactKey(trade.getTradeNo() + ":" + lineKey);
        fact.setJkyTradeNo(trade.getTradeNo());
        fact.setJkyOrderNo(trade.getOrderNo());
        fact.setOriginalOrderNo(firstNotBlank(trade.getOnlineTradeNo(), trade.getSourceTradeNo(), trade.getOrderNo()));
        fact.setGoodsLineKey(lineKey);
        fact.setBusinessDate(consignTime.toLocalDate());
        fact.setTradeTime(parseDateTime(trade.getTradeTime()));
        fact.setConsignTime(consignTime);
        fact.setCompleteTime(parseDateTime(trade.getCompleteTime()));
        fact.setSourceModifiedTime(parseDateTime(trade.getBillDate()));
        setDimensions(fact, trade, goods);
        setAmounts(fact, goods, payment, expense);
        fact.setOrderStatus(String.valueOf(trade.getTradeStatus()));
        fact.setOrderType("NORMAL");
        fact.setGiftFlag(zeroInt(goods.getIsGift()));
        fact.setRawHash(SecureUtil.sha256(JacksonUtil.toJson(trade) + "#" + index));
        return fact;
    }

    private void setDimensions(AnalysisOrderFact fact, OrderQueryRep.OrderTradeRep trade,
                               OrderQueryRep.OrderGoodsDetailRep goods) {
        fact.setSubjectName(trade.getCompanyName());
        fact.setPlatform(StrUtil.blankToDefault(trade.getShopTypeCode(), "吉客云"));
        fact.setShopId(trade.getShopId() == null ? null : String.valueOf(trade.getShopId()));
        fact.setShopName(trade.getShopName());
        fact.setWarehouseId(trade.getWarehouseId() == null ? null : String.valueOf(trade.getWarehouseId()));
        fact.setWarehouseName(trade.getWarehouseName());
        fact.setSupplierName(trade.getCompanyName());
        fact.setGoodsId(goods.getGoodsId());
        fact.setGoodsNo(firstNotBlank(goods.getGoodsNo(), goods.getOuterId()));
        fact.setGoodsName(goods.getGoodsName());
        fact.setSpecName(goods.getSpecName());
    }

    private void setAmounts(AnalysisOrderFact fact, OrderQueryRep.OrderGoodsDetailRep goods,
                            BigDecimal payment, BigDecimal expense) {
        BigDecimal quantity = firstNonNull(goods.getNeedProcessCount(), goods.getSellCount(), BigDecimal.ZERO);
        BigDecimal goodsAmount = lineWeight(goods);
        BigDecimal cost = goods.getAssessmentCost() == null ? null : goods.getAssessmentCost().multiply(quantity);
        fact.setQuantity(quantity);
        fact.setUnitPrice(firstNonNull(goods.getSellPrice(), BigDecimal.ZERO));
        fact.setGoodsAmount(goodsAmount);
        fact.setPaymentAmount(payment);
        fact.setDiscountAmount(zero(goods.getShareOrderDiscountFee()));
        fact.setPlatformSubsidy(zero(goods.getGoodsPlatDiscountFee()).add(zero(goods.getShareOrderPlatDiscountFee())));
        fact.setGovernmentSubsidy(BigDecimal.ZERO);
        fact.setOrderExpense(expense);
        fact.setAssessmentCost(goods.getAssessmentCost());
        fact.setGoodsCost(cost);
        fact.setGoodsIncentive(BigDecimal.ZERO);
        fact.setGoodsGrossProfit(cost == null ? null : payment.add(fact.getPlatformSubsidy()).subtract(cost));
        fact.setCalcStatus(cost == null ? AnalysisConstants.STATUS_INCOMPLETE : AnalysisConstants.STATUS_COMPLETE);
        fact.setMissingReason(cost == null ? "吉客云未返回商品评估成本" : null);
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

    private void persistRefunds(List<RefundQueryRep.TradeAfterOnlineWrapper> wrappers, SyncCounter counter) {
        for (RefundQueryRep.TradeAfterOnlineWrapper wrapper : wrappers) {
            if (wrapper == null || wrapper.getTradeAfterOnlineDTO() == null) {
                counter.skipCount++;
                continue;
            }
            List<AnalysisRefundFact> facts = buildRefundFacts(wrapper);
            counter.readCount += facts.size();
            for (AnalysisRefundFact fact : facts) {
                if (refundService.upsert(fact)) {
                    counter.insertCount++;
                } else {
                    counter.updateCount++;
                }
            }
        }
    }

    private List<AnalysisRefundFact> buildRefundFacts(RefundQueryRep.TradeAfterOnlineWrapper wrapper) {
        RefundQueryRep.RefundTradeRep trade = wrapper.getTradeAfterOnlineDTO();
        LocalDateTime successTime = parseDateTime(firstNotBlank(trade.getRefundSuccessTime(), trade.getGmtModified()));
        if (successTime == null || successTime.toLocalDate().isBefore(properties.getGoLiveDate())) {
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
        AnalysisRefundFact fact = new AnalysisRefundFact();
        String lineKey = firstNotBlank(goods.getGoodsId(), goods.getOuterId(), goods.getOuterSkuId(), String.valueOf(index));
        fact.setRefundKey(trade.getRefundNo() + ":" + lineKey);
        fact.setRefundNo(trade.getRefundNo());
        fact.setOriginalOrderNo(trade.getPlatOrderNo());
        fact.setGoodsLineKey(lineKey);
        fact.setGoodsNo(firstNotBlank(goods.getGoodsNo(), goods.getOuterId()));
        fact.setGoodsName(firstNotBlank(goods.getGoodsName(), goods.getTradeGoodsName()));
        fact.setRefundSuccessTime(successTime);
        fact.setRefundDate(successTime.toLocalDate());
        fact.setRefundAmount(amount);
        fact.setPlatformRefundAmount(zero(trade.getPlatRefundAmount()));
        fact.setRefundQuantity(zero(goods.getSellCount()));
        fact.setHasGoodsReturn(zeroInt(trade.getHasGoodsReturn()));
        fact.setReversedCost(BigDecimal.ZERO);
        fact.setMatchStatus(AnalysisConstants.STATUS_PENDING);
        fact.setRawHash(SecureUtil.sha256(JacksonUtil.toJson(trade) + JacksonUtil.toJson(goods)));
        return fact;
    }

    private AnalysisSyncLog startLog(LocalDate date) {
        AnalysisSyncLog log = new AnalysisSyncLog();
        log.setSyncType(AnalysisConstants.SYNC_DAILY);
        log.setWindowStart(date.atStartOfDay());
        log.setWindowEnd(date.atTime(23, 59, 59));
        log.setStatus(AnalysisConstants.STATUS_RUNNING);
        log.setReadCount(0);
        log.setInsertCount(0);
        log.setUpdateCount(0);
        log.setSkipCount(0);
        log.setStartedTime(LocalDateTime.now());
        logService.save(log);
        return log;
    }

    private void completeLog(AnalysisSyncLog log, SyncCounter counter, String status, String error) {
        log.setStatus(status);
        log.setReadCount(counter.readCount);
        log.setInsertCount(counter.insertCount);
        log.setUpdateCount(counter.updateCount);
        log.setSkipCount(counter.skipCount);
        log.setErrorMessage(StrUtil.sub(error, 0, 4000));
        log.setFinishedTime(LocalDateTime.now());
        logService.updateById(log);
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new ServiceException("同步日期不能为空");
        }
        if (date.isBefore(properties.getGoLiveDate())) {
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

    private List<OrderQueryRep.OrderGoodsDetailRep> nonGiftGoods(List<OrderQueryRep.OrderGoodsDetailRep> goods) {
        List<OrderQueryRep.OrderGoodsDetailRep> result = new ArrayList<>();
        if (goods != null) {
            for (OrderQueryRep.OrderGoodsDetailRep item : goods) {
                if (item != null && !Objects.equals(item.getIsGift(), 1)) {
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
