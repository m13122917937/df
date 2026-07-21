package com.ruoyi.job;

import com.ruoyi.user.service.WeComUserSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Quartz 企业微信用户同步任务。 */
@Component("weComUserSyncJob")
@RequiredArgsConstructor
public class WeComUserSyncJob {
    private final WeComUserSyncService userSyncService;

    /** 同步企业微信通讯录用户。 */
    public void syncUsers() {
        userSyncService.syncUsers();
    }
}
