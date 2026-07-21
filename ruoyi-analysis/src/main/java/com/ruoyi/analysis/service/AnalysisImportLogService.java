package com.ruoyi.analysis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisImportLog;
import com.ruoyi.analysis.mapper.AnalysisImportLogMapper;
import com.ruoyi.analysis.model.bo.AnalysisImportLogBO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 核算配置导入审计服务，只访问 ana_import_log。
 */
@Service
public class AnalysisImportLogService extends ServiceImpl<AnalysisImportLogMapper, AnalysisImportLog> {

    /**
     * 以独立事务保存导入审计结果，保证失败导入也可追溯。
     *
     * @param configType 配置类型
     * @param fileName 文件名
     * @param overwrite 是否覆盖
     * @param totalCount 总行数
     * @param successCount 成功行数
     * @param errorMessage 失败原因
     * @param operatorId 操作人
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void record(String configType, String fileName, boolean overwrite, int totalCount,
                       int successCount, String errorMessage, Long operatorId) {
        AnalysisImportLog log = new AnalysisImportLog();
        log.setConfigType(configType);
        log.setFileName(fileName);
        log.setOverwriteFlag(overwrite ? 1 : 0);
        log.setTotalCount(totalCount);
        log.setSuccessCount(successCount);
        log.setFailedCount(Math.max(0, totalCount - successCount));
        log.setStatus(errorMessage == null ? "SUCCESS" : "FAILED");
        log.setErrorMessage(errorMessage);
        log.setOperatorId(operatorId);
        log.setCreatedTime(LocalDateTime.now());
        log.setFinishedTime(LocalDateTime.now());
        save(log);
    }

    /**
     * 查询最近导入审计记录。
     *
     * @param limit 最大条数
     * @return 导入审计记录
     */
    public List<AnalysisImportLogBO> listRecent(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        return AnalysisConvert.INSTANCE.toImportLogBOList(baseMapper.selectRecent(safeLimit));
    }
}
