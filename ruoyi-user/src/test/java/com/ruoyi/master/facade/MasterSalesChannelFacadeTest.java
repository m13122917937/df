package com.ruoyi.master.facade;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.model.JkyResult;
import com.ruoyi.jky.param.sales.SalesChannelQueryParam;
import com.ruoyi.jky.rep.sales.SalesChannelDataRep;
import com.ruoyi.master.facade.impl.MasterSalesChannelFacade;
import com.ruoyi.master.service.MasterSalesChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 销售渠道同步规则测试。
 */
@ExtendWith(MockitoExtension.class)
class MasterSalesChannelFacadeTest {

    @Mock
    private MasterSalesChannelService masterSalesChannelService;
    @Mock
    private JkyTemplate jkyTemplate;
    @Mock
    private RedisCache redisCache;

    private MasterSalesChannelFacade facade;

    @BeforeEach
    void setUp() {
        facade = new MasterSalesChannelFacade(masterSalesChannelService, jkyTemplate, redisCache);
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(4);
            task.run();
            return true;
        }).when(redisCache).tryLockRun(anyString(), anyLong(), any(TimeUnit.class), anyString(), any(Runnable.class));
    }

    @Test
    void shouldRunFullSyncWhenRedisCursorIsMissing() {
        when(redisCache.<String>getCacheObject(anyString())).thenReturn(null);
        when(jkyTemplate.querySalesChannels(any(SalesChannelQueryParam.class))).thenReturn(successResponse());

        facade.syncSalesChannels();

        ArgumentCaptor<SalesChannelQueryParam> captor = ArgumentCaptor.forClass(SalesChannelQueryParam.class);
        verify(jkyTemplate).querySalesChannels(captor.capture());
        assertTrue(captor.getValue().getGmtModifiedStart() == null);
        verify(redisCache).setCacheObject(anyString(), anyString());
    }

    @Test
    void shouldUseRedisCursorWithFiveMinuteOverlap() {
        when(redisCache.<String>getCacheObject(anyString())).thenReturn("2026-07-26 02:30:00");
        when(jkyTemplate.querySalesChannels(any(SalesChannelQueryParam.class))).thenReturn(successResponse());

        facade.syncSalesChannels();

        ArgumentCaptor<SalesChannelQueryParam> captor = ArgumentCaptor.forClass(SalesChannelQueryParam.class);
        verify(jkyTemplate).querySalesChannels(captor.capture());
        assertEquals("2026-07-26 02:25:00", captor.getValue().getGmtModifiedStart());
        assertTrue(captor.getValue().getGmtModifiedEnd() != null);
    }

    @Test
    void shouldNotAdvanceCursorWhenJkyRequestFails() {
        when(redisCache.<String>getCacheObject(anyString())).thenReturn("2026-07-26 02:30:00");
        when(jkyTemplate.querySalesChannels(any(SalesChannelQueryParam.class))).thenReturn(new JkyResponse<>());

        assertThrows(ServiceException.class, () -> facade.syncSalesChannels());

        verify(redisCache, never()).setCacheObject(anyString(), anyString());
    }

    private JkyResponse<SalesChannelDataRep> successResponse() {
        JkyResponse<SalesChannelDataRep> response = new JkyResponse<>();
        response.setCode(200);
        SalesChannelDataRep data = new SalesChannelDataRep();
        data.setSalesChannelInfo(List.of());
        response.setResult(new JkyResult<>());
        response.getResult().setData(data);
        return response;
    }
}
