package com.ruoyi.job;

import com.ruoyi.master.facade.IMasterSalesChannelFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 吉客云销售渠道同步定时任务。
 */
@Component("masterSalesChannelSyncJob")
@RequiredArgsConstructor
public class MasterSalesChannelSyncJob {

    private final IMasterSalesChannelFacade masterSalesChannelFacade;

    /**
     * 同步吉客云销售渠道主数据。
     */
    public void syncSalesChannels() {
        masterSalesChannelFacade.syncSalesChannels();
    }
}
