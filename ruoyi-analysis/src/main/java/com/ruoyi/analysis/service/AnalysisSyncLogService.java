package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisSyncLog;
import com.ruoyi.analysis.mapper.AnalysisSyncLogMapper;
import com.ruoyi.analysis.model.bo.AnalysisSyncBO;
import org.springframework.stereotype.Service;

import java.util.List;

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
        List<AnalysisSyncLog> logs = list(new LambdaQueryWrapper<AnalysisSyncLog>()
                .orderByDesc(AnalysisSyncLog::getId)
                .last("limit " + safeLimit));
        return AnalysisConvert.INSTANCE.toSyncBOList(logs);
    }
}
