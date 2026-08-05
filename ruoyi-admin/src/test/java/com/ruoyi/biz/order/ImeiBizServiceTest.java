package com.ruoyi.biz.order;

import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.model.bo.HangingOrderBO;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.query.HangingOrderQuery;
import com.ruoyi.order.model.query.ImeiQuery;
import com.ruoyi.web.vo.order.ImeiVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 订单串码查询回归测试。
 */
@ExtendWith(MockitoExtension.class)
class ImeiBizServiceTest {

    @Mock
    private IHangingOrderFacade hangingOrderFacade;

    @Mock
    private IImeiFacade imeiFacade;

    private ImeiBizService newService() {
        ImeiBizService service = new ImeiBizService();
        ReflectionTestUtils.setField(service, "hangingOrderFacade", hangingOrderFacade);
        ReflectionTestUtils.setField(service, "imeiFacade", imeiFacade);
        return service;
    }

    @Test
    void shouldReturnImeiListWhenHangingOrderMissing() {
        String orderCode = "1cec86ac7dd19000";
        when(hangingOrderFacade.getOne(any(HangingOrderQuery.class))).thenReturn(null);
        ImeiBO imeiBO = new ImeiBO()
                .setOrderId(orderCode)
                .setImel("860000000000000")
                .setSn("SN001");
        when(imeiFacade.list(any(ImeiQuery.class))).thenReturn(Collections.singletonList(imeiBO));

        List<ImeiVO> result = newService().list(orderCode);

        assertEquals(1, result.size());
        assertEquals("860000000000000", result.get(0).getImel());
        ArgumentCaptor<ImeiQuery> captor = ArgumentCaptor.forClass(ImeiQuery.class);
        verify(imeiFacade).list(captor.capture());
        assertEquals(orderCode, captor.getValue().getOrderId());
        assertNull(captor.getValue().getHangingOrderId());
    }

    @Test
    void shouldFilterByHangingOrderWhenPresent() {
        String orderCode = "ORDER-2";
        HangingOrderBO hangingOrderBO = new HangingOrderBO().setId(99L).setOrderId(orderCode);
        when(hangingOrderFacade.getOne(any(HangingOrderQuery.class))).thenReturn(hangingOrderBO);
        when(imeiFacade.list(any(ImeiQuery.class))).thenReturn(Collections.emptyList());

        newService().list(orderCode);

        ArgumentCaptor<ImeiQuery> captor = ArgumentCaptor.forClass(ImeiQuery.class);
        verify(imeiFacade).list(captor.capture());
        assertEquals(Long.valueOf(99L), captor.getValue().getHangingOrderId());
    }
}
