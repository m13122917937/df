package com.ruoyi.master.facade;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.rep.sales.SalesChannelDataRep;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 吉客云销售渠道响应结构测试。
 */
class SalesChannelResponseTest {

    /**
     * 销售渠道列表应从 data.salesChannelInfo 读取。
     */
    @Test
    void shouldReadSalesChannelsFromObjectResponseData() {
        String body = "{\"code\":200,\"result\":{\"data\":{\"salesChannelInfo\":[{\"channelId\":\"1\"}]}}}";

        JkyResponse<SalesChannelDataRep> response = JacksonUtil.parse(body,
                new TypeReference<JkyResponse<SalesChannelDataRep>>() { });

        assertEquals(1, response.getResult().getData().getSalesChannelInfo().size());
        assertEquals(1L, response.getResult().getData().getSalesChannelInfo().get(0).getChannelId());
    }
}
