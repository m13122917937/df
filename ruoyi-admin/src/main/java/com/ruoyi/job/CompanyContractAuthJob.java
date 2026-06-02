package com.ruoyi.job;

import com.ruoyi.biz.company.CompanyBizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 企业合同认证状态定时任务
 */
@Slf4j
@Component("companyContractAuthJob")
public class CompanyContractAuthJob {

    @Autowired
    private CompanyBizService companyBizService;

    /**
     * 执行企业合同认证状态同步
     */
    public void execute() {
        log.info("开始执行企业合同认证状态定时任务");
        companyBizService.syncContractAuthStatus();
        log.info("结束执行企业合同认证状态定时任务");
    }
}
