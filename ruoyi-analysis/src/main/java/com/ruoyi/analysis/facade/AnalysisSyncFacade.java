package com.ruoyi.analysis.facade;

import com.ruoyi.analysis.model.bo.AnalysisSyncBO;

import java.time.LocalDate;
import java.util.List;

/**
 * 经营分析同步领域对外接口。
 */
public interface AnalysisSyncFacade {

    /**
     * 同步指定自然日。
     *
     * @param date 日期
     * @return 同步结果
     */
    AnalysisSyncBO sync(LocalDate date);

    /**
     * 重算指定日期经营指标。
     *
     * @param date 日期
     */
    void rebuild(LocalDate date);

    /**
     * 查询最近同步日志。
     *
     * @param limit 最大条数
     * @return 同步日志
     */
    List<AnalysisSyncBO> logs(int limit);

    /**
     * 执行定时同步，优先处理最近失败日期，否则同步昨日。
     */
    void syncScheduled();
}
