package com.ruoyi.order.service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.order.domain.HangingOrder;
import com.ruoyi.order.domain.Order;
import com.ruoyi.order.domain.TradeOrder;
import com.ruoyi.order.mapper.HangingOrderMapper;
import com.ruoyi.order.mapper.OrderMapper;
import com.ruoyi.order.mapper.TradeOrderMapper;
import com.ruoyi.mapper.order.OrderConvert;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.consts.TradeOrderConsts;
import com.ruoyi.order.model.param.SupplierPushParam;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.bo.MemberBO;
import com.ruoyi.web.form.order.WaitPushForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 定向推送供应商事务服务测试。
 */
@ExtendWith(MockitoExtension.class)
class OrderSupplierPushServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private HangingOrderMapper hangingOrderMapper;

    @Mock
    private TradeOrderMapper tradeOrderMapper;

    private OrderSupplierPushService service;

    @BeforeEach
    void setUp() {
        service = new OrderSupplierPushService(orderMapper, hangingOrderMapper, tradeOrderMapper);
    }

    @Test
    void shouldCreateOneAssignmentForFirstPush() {
        Order order = order("ORDER-1", OrderConsts.OrderStatus.NEW.getCode());
        when(orderMapper.selectByOrderCodesForUpdate(Collections.singletonList("ORDER-1")))
                .thenReturn(Collections.singletonList(order));
        when(tradeOrderMapper.selectActiveByOrderCodesForUpdate(anyList(),
                eq(TradeOrderConsts.TradeStatus.SUCCESS.getCode()))).thenReturn(Collections.emptyList());
        doAnswer(invocation -> {
            HangingOrder hangingOrder = invocation.getArgument(0);
            hangingOrder.setId(100L);
            return 1;
        }).when(hangingOrderMapper).insert(any(HangingOrder.class));

        service.pushSupplierBatch(param(Collections.singletonList("ORDER-1"), 35L));

        verify(hangingOrderMapper).insert(any(HangingOrder.class));
        verify(tradeOrderMapper).insert(any(TradeOrder.class));
        verify(orderMapper).updateById(any(Order.class));
    }

    @Test
    void shouldTreatSameSupplierAsIdempotentSuccess() {
        Order order = order("ORDER-1", OrderConsts.OrderStatus.DELIVERY_ING.getCode());
        TradeOrder activeTrade = activeTrade("ORDER-1", 35L, "供应商甲");
        when(orderMapper.selectByOrderCodesForUpdate(Collections.singletonList("ORDER-1")))
                .thenReturn(Collections.singletonList(order));
        when(tradeOrderMapper.selectActiveByOrderCodesForUpdate(anyList(),
                eq(TradeOrderConsts.TradeStatus.SUCCESS.getCode())))
                .thenReturn(Collections.singletonList(activeTrade));

        service.pushSupplierBatch(param(Collections.singletonList("ORDER-1"), 35L));

        verify(hangingOrderMapper, never()).insert(any(HangingOrder.class));
        verify(tradeOrderMapper, never()).insert(any(TradeOrder.class));
        verify(orderMapper, never()).updateById(any(Order.class));
    }

    @Test
    void shouldRejectDifferentSupplier() {
        Order order = order("ORDER-1", OrderConsts.OrderStatus.DELIVERY_ING.getCode());
        TradeOrder activeTrade = activeTrade("ORDER-1", 35L, "供应商甲");
        when(orderMapper.selectByOrderCodesForUpdate(Collections.singletonList("ORDER-1")))
                .thenReturn(Collections.singletonList(order));
        when(tradeOrderMapper.selectActiveByOrderCodesForUpdate(anyList(),
                eq(TradeOrderConsts.TradeStatus.SUCCESS.getCode())))
                .thenReturn(Collections.singletonList(activeTrade));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> service.pushSupplierBatch(param(Collections.singletonList("ORDER-1"), 102L)));

        assertEquals("订单已推送给供应商 供应商甲，不能推送给其他供应商", exception.getMessage());
        verify(hangingOrderMapper, never()).insert(any(HangingOrder.class));
    }

    @Test
    void shouldRejectInactiveTradeWhenOrderHasNotReturnedToPushableState() {
        Order order = order("ORDER-1", OrderConsts.OrderStatus.CHASE_ORDER.getCode());
        when(orderMapper.selectByOrderCodesForUpdate(Collections.singletonList("ORDER-1")))
                .thenReturn(Collections.singletonList(order));
        when(tradeOrderMapper.selectActiveByOrderCodesForUpdate(anyList(),
                eq(TradeOrderConsts.TradeStatus.SUCCESS.getCode()))).thenReturn(Collections.emptyList());

        ServiceException exception = assertThrows(ServiceException.class,
                () -> service.pushSupplierBatch(param(Collections.singletonList("ORDER-1"), 35L)));

        assertEquals("订单状态不允许推送：ORDER-1", exception.getMessage());
        verify(hangingOrderMapper, never()).insert(any(HangingOrder.class));
    }

    @Test
    void shouldValidateWholeBatchBeforeWriting() {
        Order first = order("ORDER-1", OrderConsts.OrderStatus.NEW.getCode());
        Order second = order("ORDER-2", OrderConsts.OrderStatus.DELIVERY_ING.getCode());
        when(orderMapper.selectByOrderCodesForUpdate(Arrays.asList("ORDER-1", "ORDER-2")))
                .thenReturn(Arrays.asList(first, second));
        when(tradeOrderMapper.selectActiveByOrderCodesForUpdate(anyList(),
                eq(TradeOrderConsts.TradeStatus.SUCCESS.getCode())))
                .thenReturn(Collections.singletonList(activeTrade("ORDER-2", 35L, "供应商甲")));

        assertThrows(ServiceException.class,
                () -> service.pushSupplierBatch(param(Arrays.asList("ORDER-1", "ORDER-2"), 102L)));

        verify(hangingOrderMapper, never()).insert(any(HangingOrder.class));
        verify(tradeOrderMapper, never()).insert(any(TradeOrder.class));
        verify(orderMapper, never()).updateById(any(Order.class));
    }

    @Test
    void shouldConvertWebInputWithMapStruct() {
        WaitPushForm form = new WaitPushForm();
        form.setOrderCodeList(Collections.singletonList("ORDER-1"));
        form.setCompanyId(35L);
        form.setUserId(10L);
        form.setPrice(BigDecimal.valueOf(100));
        form.setDeliveryTime(1);
        form.setAccountingPeriod(30);
        MemberBO member = new MemberBO();
        member.setUserId(10L);
        member.setNickName("推单用户");
        member.setPhone("13800000000");
        CompanyBO company = new CompanyBO();
        company.setId(35L);
        company.setCompanyName("供应商甲");
        company.setNickName("甲");

        SupplierPushParam converted = OrderConvert.INSTANCE.toSupplierPushParam(form, member, company, 1L);

        assertEquals(35L, converted.getCompanyId());
        assertEquals("供应商甲", converted.getCompanyName());
        assertEquals("13800000000", converted.getUserPhone());
        assertEquals(1L, converted.getOperatorId());
    }

    private Order order(String orderCode, Integer status) {
        return new Order()
                .setOrderCode(orderCode)
                .setStatus(status)
                .setSkuCode("SKU-1")
                .setOrderType(2)
                .setQuantity(1L);
    }

    private TradeOrder activeTrade(String orderCode, Long companyId, String supplierName) {
        return new TradeOrder()
                .setOrderId(orderCode)
                .setTradeCompanyId(companyId)
                .setTradeNickName(supplierName)
                .setStatus((long) TradeOrderConsts.TradeStatus.SUCCESS.getCode());
    }

    private SupplierPushParam param(java.util.List<String> orderCodes, Long companyId) {
        return new SupplierPushParam()
                .setOrderCodeList(orderCodes)
                .setCompanyId(companyId)
                .setCompanyName("供应商")
                .setCompanyNickName("供应商")
                .setUserId(10L)
                .setUserName("推单用户")
                .setUserPhone("13800000000")
                .setPrice(BigDecimal.valueOf(100))
                .setDeliveryTime(1)
                .setAccountingPeriod(30)
                .setOperatorId(1L);
    }
}
