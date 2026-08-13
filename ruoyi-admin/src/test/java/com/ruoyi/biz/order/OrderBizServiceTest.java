package com.ruoyi.biz.order;

import com.ruoyi.biz.address.SmartParse;
import com.ruoyi.biz.company.CompanyCapitalBizService;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.express.facade.IRouteSubscribeFacade;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.master.facade.IMasterSubjectBankFacade;
import com.ruoyi.master.facade.IMasterSubjectFacade;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.product.facade.IProductSkuFacade;
import com.ruoyi.biz.sys.IDictDistrictBizService;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.facade.IMemberFacade;
import com.ruoyi.web.form.order.OrderAddForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regression tests for repeated order synchronization.
 */
@ExtendWith(MockitoExtension.class)
class OrderBizServiceTest {

    @Mock
    private SmartParse smartParse;

    @Mock
    private IOrderFacade orderFacade;

    @Test
    void shouldRefreshErpOrderIdWhenOrderAlreadyExists() {
        OrderBO existingOrder = new OrderBO().setOrderCode("ORDER-001")
                .setStatus(OrderConsts.OrderStatus.NEW.getCode()).setErpOrderId("ERP-OLD");
        when(orderFacade.getOne(any(OrderQuery.class))).thenReturn(existingOrder);
        when(smartParse.parseAddressInfo("test-address")).thenReturn(null);

        newService().add(buildForm());

        ArgumentCaptor<OrderParam> paramCaptor = ArgumentCaptor.forClass(OrderParam.class);
        ArgumentCaptor<OrderQuery> queryCaptor = ArgumentCaptor.forClass(OrderQuery.class);
        verify(orderFacade).update(paramCaptor.capture(), queryCaptor.capture());
        assertEquals("ERP-NEW", paramCaptor.getValue().getErpOrderId());
        assertEquals("ORDER-001", queryCaptor.getValue().getOrderCode());
    }

    @Test
    void shouldRefreshErpOrderIdWhenRevokedOrderIsRestored() {
        OrderBO existingOrder = new OrderBO().setOrderCode("ORDER-002")
                .setStatus(OrderConsts.OrderStatus.REVOKE.getCode()).setErpOrderId("ERP-OLD");
        when(orderFacade.getOne(any(OrderQuery.class))).thenReturn(existingOrder);
        when(smartParse.parseAddressInfo("test-address")).thenReturn(null);

        newService().add(buildForm());

        ArgumentCaptor<OrderParam> paramCaptor = ArgumentCaptor.forClass(OrderParam.class);
        verify(orderFacade).update(paramCaptor.capture(), any(OrderQuery.class));
        assertEquals("ERP-NEW", paramCaptor.getValue().getErpOrderId());
        assertEquals(OrderConsts.OrderStatus.NEW.getCode(), paramCaptor.getValue().getStatus());
    }

    private OrderBizService newService() {
        return new OrderBizService(smartParse, orderFacade, mock(ITradeOrderFacade.class),
                mock(IHangingOrderFacade.class), mock(IDictDistrictBizService.class), mock(RuleBizService.class),
                mock(ICompanyCapitalFacade.class), mock(CompanyCapitalBizService.class), mock(IProductSkuFacade.class),
                mock(IRouteSubscribeFacade.class), mock(IImeiFacade.class), mock(ICompanyFacade.class),
                mock(IMasterSubjectBankFacade.class), mock(IMasterSubjectFacade.class), mock(IMemberFacade.class),
                mock(JkyTemplate.class));
    }

    private OrderAddForm buildForm() {
        return new OrderAddForm().setErpOrderId("ERP-NEW").setOriginalOrderId("ORIGINAL-001")
                .setJkyTradeNo("JY-001").setAddress("test-address")
                .setAddressee("tester").setPhone("13800000000");
    }
}
