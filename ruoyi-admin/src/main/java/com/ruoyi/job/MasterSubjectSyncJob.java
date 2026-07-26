package com.ruoyi.job;

import com.ruoyi.biz.master.MasterSubjectBankBizService;
import com.ruoyi.master.facade.IMasterSubjectFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 吉客云经营主体同步定时任务。
 */
@Slf4j
@Component("masterSubjectSyncJob")
@RequiredArgsConstructor
public class MasterSubjectSyncJob {

    private final IMasterSubjectFacade masterSubjectFacade;
    private final MasterSubjectBankBizService masterSubjectBankBizService;

    /**
     * 由定时任务中心触发经营主体同步，并为缺失默认卡的主体自动补设。
     */
    public void syncSubjects() {
        log.info("开始执行吉客云经营主体同步任务");
        masterSubjectFacade.syncSubjects();
        try {
            masterSubjectBankBizService.autoFillDefaultBank();
        } catch (Exception e) {
            log.error("经营主体默认银行卡自动补设异常", e);
        }
        log.info("吉客云经营主体同步任务执行完成");
    }
}
