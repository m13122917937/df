package com.ruoyi.biz.analysis;

import com.ruoyi.analysis.facade.AnalysisSyncFacade;
import com.ruoyi.analysis.model.bo.AnalysisSyncBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 经营分析同步与重算业务编排。
 */
@Component
public class AnalysisSyncBizService {
    @Autowired
    private AnalysisSyncFacade syncFacade;

    /**
     * 手动同步指定自然日。
     */
    public AnalysisSyncBO sync(LocalDate date) {
        return syncFacade.sync(date);
    }

    /**
     * 重算指定日期快照。
     */
    public void rebuild(LocalDate date) {
        syncFacade.rebuild(date);
    }
}
