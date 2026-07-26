package com.ruoyi.order.service;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.order.convert.SupplierPushCov;
import com.ruoyi.order.domain.HangingOrder;
import com.ruoyi.order.domain.Order;
import com.ruoyi.order.domain.TradeOrder;
import com.ruoyi.order.mapper.HangingOrderMapper;
import com.ruoyi.order.mapper.OrderMapper;
import com.ruoyi.order.mapper.TradeOrderMapper;
import com.ruoyi.order.model.consts.HandingOrderConsts;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.SupplierPushParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 定向推送供应商事务服务。
 */
@Service
public class OrderSupplierPushService {

    private static final BigDecimal PRICE_STEP = BigDecimal.TEN;

    private final OrderMapper orderMapper;
    private final HangingOrderMapper hangingOrderMapper;
    private final TradeOrderMapper tradeOrderMapper;

    /**
     * 创建定向推送供应商事务服务。
     *
     * @param orderMapper 订单数据访问
     * @param hangingOrderMapper 挂单数据访问
     * @param tradeOrderMapper 成交数据访问
     */
    public OrderSupplierPushService(OrderMapper orderMapper,
                                    HangingOrderMapper hangingOrderMapper,
                                    TradeOrderMapper tradeOrderMapper) {
        this.orderMapper = orderMapper;
        this.hangingOrderMapper = hangingOrderMapper;
        this.tradeOrderMapper = tradeOrderMapper;
    }

    /**
     * 批量定向推送供应商。
     *
     * @param param 推送参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void pushSupplierBatch(SupplierPushParam param) {
        List<String> orderCodes = normalizeOrderCodes(param.getOrderCodeList());
        List<Order> orders = orderMapper.selectByOrderCodesForUpdate(orderCodes);
        validateOrders(orderCodes, orders);
        Map<String, TradeOrder> activeTradeMap = loadActiveTradeMap(orderCodes);
        Set<String> idempotentOrders = validateAssignments(orders, activeTradeMap, param);
        Date operateTime = DateUtil.date();
        for (Order order : orders) {
            if (!idempotentOrders.contains(order.getOrderCode())) {
                createAssignment(order, param, operateTime);
            }
        }
    }

    private List<String> normalizeOrderCodes(List<String> orderCodes) {
        if (orderCodes == null) {
            throw new ServiceException("请选择需要推送的订单");
        }
        List<String> normalized = orderCodes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        if (normalized.isEmpty()) {
            throw new ServiceException("请选择需要推送的订单");
        }
        return normalized;
    }

    private void validateOrders(List<String> orderCodes, List<Order> orders) {
        if (orders.size() != orderCodes.size()) {
            Set<String> foundCodes = orders.stream().map(Order::getOrderCode).collect(Collectors.toSet());
            List<String> missingCodes = orderCodes.stream()
                    .filter(code -> !foundCodes.contains(code))
                    .collect(Collectors.toList());
            throw new ServiceException("订单不存在或已删除：" + String.join(",", missingCodes));
        }
        long skuCount = orders.stream().map(Order::getSkuCode).distinct().count();
        if (skuCount > 1) {
            throw new ServiceException("请勿推送不同SKU的订单");
        }
    }

    private Map<String, TradeOrder> loadActiveTradeMap(List<String> orderCodes) {
        List<TradeOrder> activeTrades = tradeOrderMapper.selectActiveByOrderCodesForUpdate(
                orderCodes, TradeOrderConsts.TradeStatus.SUCCESS.getCode());
        Map<String, TradeOrder> activeTradeMap = new HashMap<>();
        for (TradeOrder tradeOrder : activeTrades) {
            if (activeTradeMap.put(tradeOrder.getOrderId(), tradeOrder) != null) {
                throw new ServiceException("订单存在多条有效成交记录：" + tradeOrder.getOrderId());
            }
        }
        return activeTradeMap;
    }

    private Set<String> validateAssignments(List<Order> orders,
                                            Map<String, TradeOrder> activeTradeMap,
                                            SupplierPushParam param) {
        Set<String> idempotentOrders = new HashSet<>();
        for (Order order : orders) {
            TradeOrder activeTrade = activeTradeMap.get(order.getOrderCode());
            if (activeTrade != null) {
                validateActiveTrade(activeTrade, param, idempotentOrders);
            } else if (!isPushable(order)) {
                throw new ServiceException("订单状态不允许推送：" + order.getOrderCode());
            }
        }
        return idempotentOrders;
    }

    private void validateActiveTrade(TradeOrder activeTrade,
                                     SupplierPushParam param,
                                     Set<String> idempotentOrders) {
        if (Objects.equals(activeTrade.getTradeCompanyId(), param.getCompanyId())) {
            idempotentOrders.add(activeTrade.getOrderId());
            return;
        }
        String supplierName = activeTrade.getTradeNickName();
        if (supplierName == null || supplierName.trim().isEmpty()) {
            supplierName = activeTrade.getTradeCompanyName();
        }
        if (supplierName == null || supplierName.trim().isEmpty()) {
            supplierName = String.valueOf(activeTrade.getTradeCompanyId());
        }
        throw new ServiceException("订单已推送给供应商 " + supplierName + "，不能推送给其他供应商");
    }

    private boolean isPushable(Order order) {
        return Objects.equals(order.getStatus(), OrderConsts.OrderStatus.NEW.getCode())
                || Objects.equals(order.getStatus(), OrderConsts.OrderStatus.WAIT.getCode());
    }

    private void createAssignment(Order order, SupplierPushParam param, Date operateTime) {
        hangingOrderMapper.expireActiveByOrderCode(order.getOrderCode(),
                HandingOrderConsts.Status.NORMAL.getCode(),
                HandingOrderConsts.Status.FAILURE.getCode(),
                param.getOperatorId(), operateTime);
        HangingOrder hangingOrder = createHangingOrder(order, param, operateTime);
        hangingOrderMapper.insert(hangingOrder);
        tradeOrderMapper.insert(createTradeOrder(order, param, hangingOrder.getId(), operateTime));
        updateOrderState(order.getOrderCode(), operateTime);
    }

    private HangingOrder createHangingOrder(Order order, SupplierPushParam param, Date operateTime) {
        HangingOrder hangingOrder = SupplierPushCov.INSTANCE.toHangingOrder(param, order);
        return hangingOrder
                .setPriceHighestStatus((long) TradeOrderConsts.TradeStatus.SUCCESS.getCode())
                .setPriceHign(param.getPrice().subtract(PRICE_STEP))
                .setPriceHignStatus((long) TradeOrderConsts.TradeStatus.CONFIRMED.getCode())
                .setPriceLow(param.getPrice().subtract(PRICE_STEP.multiply(BigDecimal.valueOf(2))))
                .setPriceLowStatus((long) TradeOrderConsts.TradeStatus.CONFIRMED.getCode())
                .setPriceLowest(param.getPrice().subtract(PRICE_STEP.multiply(BigDecimal.valueOf(3))))
                .setPriceLowestStatus((long) TradeOrderConsts.TradeStatus.CONFIRMED.getCode())
                .setQuotationInterval(5L)
                .setStatus(HandingOrderConsts.Status.NORMAL.getCode())
                .setLastCompeteTime(operateTime)
                .setCreateBy(param.getOperatorId())
                .setCreateTime(operateTime)
                .setUpdateBy(param.getOperatorId())
                .setUpdateTime(operateTime)
                .setIntervalSpread(PRICE_STEP)
                .setCodeOptions((long) HandingOrderConsts.CodeOptions.SEND_BEFORE_NEED.getCode())
                .setDeliveryDeadline(DateUtil.offsetDay(DateUtil.endOfDay(operateTime), param.getDeliveryTime()));
    }

    private TradeOrder createTradeOrder(Order order,
                                        SupplierPushParam param,
                                        Long hangingOrderId,
                                        Date operateTime) {
        return SupplierPushCov.INSTANCE.toTradeOrder(param, order)
                .setHangOrderId(hangingOrderId)
                .setStatus((long) TradeOrderConsts.TradeStatus.SUCCESS.getCode())
                .setTradeIndex(4L)
                .setCreateTime(operateTime)
                .setUpdateBy(param.getOperatorId())
                .setUpdateTime(operateTime);
    }

    private void updateOrderState(String orderCode, Date operateTime) {
        Order order = new Order()
                .setOrderCode(orderCode)
                .setStatus(OrderConsts.OrderStatus.DELIVERY_ING.getCode())
                .setSubStatus((long) OrderConsts.OrderSubStatus.WAIT_IMEI.getCode())
                .setUpdateTime(operateTime);
        orderMapper.updateById(order);
    }
}
