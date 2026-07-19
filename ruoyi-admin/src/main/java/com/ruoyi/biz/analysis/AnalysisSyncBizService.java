package com.ruoyi.biz.analysis;

import com.ruoyi.analysis.facade.AnalysisSyncFacade;
import com.ruoyi.analysis.model.bo.AnalysisSyncBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 经营分析同步与重算业务编排。
 */
@Component
public class AnalysisSyncBizService {
    @Autowired
    private AnalysisSyncFacade syncFacade;

    /**
     * 手动同步指定自然日。
     *
     * @param date 日期
     * @return 同步结果
     */
    public AnalysisSyncBO sync(LocalDate date) {
        return syncFacade.sync(date);
    }

    /**
     * 重算指定日期快照。
     *
     * @param date 日期
     */
    public void rebuild(LocalDate date) {
        syncFacade.rebuild(date);
    }

    /**
     * 查询同步日志。
     *
     * @param limit 最大条数
     * @return 同步日志
     */
    public List<AnalysisSyncBO> logs(int limit) {
        return syncFacade.logs(limit);
    }
}
