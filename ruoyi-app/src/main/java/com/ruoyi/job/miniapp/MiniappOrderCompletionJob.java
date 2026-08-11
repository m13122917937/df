package com.ruoyi.job.miniapp;

import com.ruoyi.subsidy.facade.IGbOrderFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 国补订单自动完成定时入口。 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MiniappOrderCompletionJob {
    private final IGbOrderFacade orderFacade;

    /** 每日凌晨自动完成发货满七天的订单。 */
    @Scheduled(cron = "0 10 0 * * ?")
    public void completeExpiredShipments() {
        int completedCount = orderFacade.completeExpiredShipments();
        log.info("国补订单自动完成任务结束，完成数量={}", completedCount);
    }
}
