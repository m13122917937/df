package com.ruoyi.master.convert;

import com.ruoyi.jky.rep.sales.SalesChannelRep;
import com.ruoyi.master.domain.MasterSalesChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 销售渠道对象转换测试。
 */
class MasterSalesChannelConvertTest {

    /**
     * 吉客云渠道字段应转换为销售渠道字段。
     */
    @Test
    void shouldConvertJkySalesChannelToDomain() {
        SalesChannelRep source = new SalesChannelRep();
        source.setChannelId(1001L);
        source.setChannelCode("SHOP-001");
        source.setChannelName("测试店铺");
        source.setOnlinePlatTypeName("拼多多");
        source.setCompanyName("测试主体");

        MasterSalesChannel channel = MasterSalesChannelConvert.INSTANCE.toDomain(source);

        assertEquals(1001L, channel.getJkyChannelId());
        assertEquals("SHOP-001", channel.getChannelCode());
        assertEquals("拼多多", channel.getPlatformName());
        assertEquals("测试主体", channel.getSubjectName());
    }
}
