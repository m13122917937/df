package com.ruoyi.order.facade;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.order.domain.HangingOrder;
import com.ruoyi.order.facade.impl.HangingOrderFacade;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.service.HangingOrderService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 挂单领域门面测试。
 */
class HangingOrderFacadeTest {

    @Test
    void shouldKeepCreateTimeAndWriteUpdateTime() {
        HangingOrderService service = mock(HangingOrderService.class);
        HangingOrderFacade facade = new HangingOrderFacade();
        ReflectionTestUtils.setField(facade, "hangingOrderService", service);
        Date createTime = new Date(1_000L);
        HangingOrderParam param = new HangingOrderParam()
                .setStatus(2)
                .setCreateTime(createTime);
        when(service.update(any(HangingOrder.class), any(Wrapper.class))).thenReturn(true);

        facade.update(param, new HangingOrderQuery().setOrderId("ORDER-1"));

        ArgumentCaptor<HangingOrder> captor = ArgumentCaptor.forClass(HangingOrder.class);
        verify(service).update(captor.capture(), any(Wrapper.class));
        assertEquals(createTime, captor.getValue().getCreateTime());
        assertNotNull(captor.getValue().getUpdateTime());
    }
}
