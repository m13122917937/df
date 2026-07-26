package com.ruoyi.job;

import com.ruoyi.biz.bill.BillBizService;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.jky.JkyTemplate;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.model.JkyResult;
import com.ruoyi.jky.rep.logistics.LogisticsUpdateRep;
import com.ruoyi.order.facade.IJkyLogisticsTaskFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.model.bo.JkyLogisticsTaskBO;
import com.ruoyi.order.model.param.JkyLogisticsTaskParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 吉客云物流延迟任务重试测试。
 */
@ExtendWith(MockitoExtension.class)
class JkyLogisticsTaskJobTest {

    @Mock
    private IJkyLogisticsTaskFacade jkyLogisticsTaskFacade;

    @Mock
    private JkyTemplate jkyTemplate;

    @Mock
    private RedisCache redisCache;

    @Mock
    private IOrderFacade orderFacade;

    @Mock
    private BillBizService billBizService;

    private JkyLogisticsTaskJob job;

    @BeforeEach
    void setUp() {
        job = new JkyLogisticsTaskJob();
        ReflectionTestUtils.setField(job, "jkyLogisticsTaskFacade", jkyLogisticsTaskFacade);
        ReflectionTestUtils.setField(job, "jkyTemplate", jkyTemplate);
        ReflectionTestUtils.setField(job, "redisCache", redisCache);
        ReflectionTestUtils.setField(job, "orderFacade", orderFacade);
        ReflectionTestUtils.setField(job, "billBizService", billBizService);
        when(jkyTemplate.updateLogisticsInfo(any())).thenReturn(failedResponse());
    }

    @Test
    void shouldScheduleFirstRetryAfterFiveMinutes() {
        assertRetryScheduled(0, 1, 5);
    }

    @Test
    void shouldScheduleSecondRetryAfterTenMinutes() {
        assertRetryScheduled(1, 2, 10);
    }

    @Test
    void shouldScheduleThirdRetryAfterTwentyMinutes() {
        assertRetryScheduled(2, 3, 20);
    }

    @Test
    void shouldMarkTaskFailedAfterThirdRetryFails() {
        ReflectionTestUtils.invokeMethod(job, "processTask", task(3));

        ArgumentCaptor<JkyLogisticsTaskParam> captor = ArgumentCaptor.forClass(JkyLogisticsTaskParam.class);
        verify(jkyLogisticsTaskFacade).update(captor.capture(), any());
        assertEquals(2, captor.getValue().getStatus());
        assertEquals("物流更新失败", captor.getValue().getErrorMsg());
        assertNull(captor.getValue().getRetryCount());
        assertNull(captor.getValue().getExecuteTime());
    }

    private void assertRetryScheduled(int currentRetryCount, int expectedRetryCount, int delayMinutes) {
        Date before = new Date();
        ReflectionTestUtils.invokeMethod(job, "processTask", task(currentRetryCount));
        Date after = new Date();

        ArgumentCaptor<JkyLogisticsTaskParam> captor = ArgumentCaptor.forClass(JkyLogisticsTaskParam.class);
        verify(jkyLogisticsTaskFacade).update(captor.capture(), any());
        JkyLogisticsTaskParam param = captor.getValue();
        assertEquals(0, param.getStatus());
        assertEquals(expectedRetryCount, param.getRetryCount());
        assertEquals("物流更新失败", param.getErrorMsg());
        assertTrue(param.getExecuteTime().getTime() >= before.getTime() + delayMinutes * 60_000L);
        assertTrue(param.getExecuteTime().getTime() <= after.getTime() + delayMinutes * 60_000L);
    }

    private JkyLogisticsTaskBO task(int retryCount) {
        return new JkyLogisticsTaskBO().setId(1L).setOrderCode("ORDER-1").setRetryCount(retryCount);
    }

    private JkyResponse<List<LogisticsUpdateRep>> failedResponse() {
        LogisticsUpdateRep failedResult = new LogisticsUpdateRep();
        failedResult.setIsSuccess(false);
        failedResult.setError("物流更新失败");
        JkyResult<List<LogisticsUpdateRep>> result = new JkyResult<>();
        result.setData(Collections.singletonList(failedResult));
        JkyResponse<List<LogisticsUpdateRep>> response = new JkyResponse<>();
        response.setCode(200);
        response.setResult(result);
        return response;
    }
}
