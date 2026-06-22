package com.ruoyi.job;

import com.ruoyi.biz.contract.ContractBizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 合同签署状态同步定时任务（兜底拉取）
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Slf4j
@Component("contractStatusSyncJob")
public class ContractStatusSyncJob {

    @Autowired
    private ContractBizService contractBizService;

    /**
     * 扫描所有签署中合同，逐个拉取 e 签宝最新状态
     */
    public void execute() {
        log.info("开始执行合同状态同步定时任务");
        contractBizService.syncAllSigning();
        log.info("结束执行合同状态同步定时任务");
    }
}
