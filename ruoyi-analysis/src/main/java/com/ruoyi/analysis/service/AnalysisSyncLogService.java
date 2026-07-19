package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.constant.AnalysisConstants;
import com.ruoyi.analysis.domain.AnalysisSyncLog;
import com.ruoyi.analysis.mapper.AnalysisSyncLogMapper;
import com.ruoyi.analysis.model.bo.AnalysisSyncBO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * 经营同步日志服务，只访问 ana_sync_log。
 */
@Service
public class AnalysisSyncLogService extends ServiceImpl<AnalysisSyncLogMapper, AnalysisSyncLog> {

    /**
     * 查询最近同步日志。
     *
     * @param limit 最大条数
     * @return 同步日志
     */
    public List<AnalysisSyncBO> listRecent(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        List<AnalysisSyncLog> logs = baseMapper.selectRecent(safeLimit);
        return AnalysisConvert.INSTANCE.toSyncBOList(logs);
    }

    /**
     * 查找仍未成功重跑的最近失败日期。
     *
     * @return 待重试日期，没有则返回 null
     */
    public LocalDate findPendingRetryDate() {
        List<AnalysisSyncLog> logs = baseMapper.selectRecent(200);
        Set<LocalDate> completed = new HashSet<>();
        for (AnalysisSyncLog log : logs) {
            LocalDate date = log.getWindowStart().toLocalDate();
            if (AnalysisConstants.STATUS_COMPLETE.equals(log.getStatus())) {
                completed.add(date);
            } else if (AnalysisConstants.STATUS_FAILED.equals(log.getStatus()) && !completed.contains(date)) {
                return date;
            }
        }
        return null;
    }
}
