package com.ruoyi.analysis.facade.impl;

import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.convert.AnalysisQueryConvert;
import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.facade.AnalysisConfigFacade;
import com.ruoyi.analysis.model.bo.AnalysisCostConfigBO;
import com.ruoyi.analysis.model.bo.AnalysisImportLogBO;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import com.ruoyi.analysis.service.AnalysisCostConfigService;
import com.ruoyi.analysis.service.AnalysisImportLogService;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.framework.mybatis.DynamicCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 经营核算配置领域接口实现。
 */
@Component
public class AnalysisConfigFacadeImpl implements AnalysisConfigFacade {
    @Autowired
    private AnalysisCostConfigService configService;
    @Autowired
    private AnalysisImportLogService importLogService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AnalysisCostConfigBO> list(AnalysisQuery query) {
        List<AnalysisCostConfig> configs = configService.list(DynamicCondition.toWrapper(
                AnalysisQueryConvert.INSTANCE.toCostConfigQuery(query), SortBy.of("-business_date,-id")));
        return AnalysisConvert.INSTANCE.toConfigBOList(configs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(AnalysisCostConfigParam param) {
        return configService.saveConfig(param);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        configService.deleteConfig(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int importConfigs(List<AnalysisCostConfigParam> params, boolean overwrite, String fileName, Long operatorId) {
        String configType = params.isEmpty() ? "UNKNOWN" : params.get(0).getConfigType();
        try {
            int successCount = configService.importConfigs(params, overwrite);
            importLogService.record(configType, fileName, overwrite, params.size(), successCount, null, operatorId);
            return successCount;
        } catch (RuntimeException exception) {
            importLogService.record(configType, fileName, overwrite, params.size(), 0,
                    exception.getMessage(), operatorId);
            throw exception;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordImportFailure(String configType, String fileName, boolean overwrite, int totalCount,
                                    String errorMessage, Long operatorId) {
        importLogService.record(configType, fileName, overwrite, totalCount, 0, errorMessage, operatorId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AnalysisImportLogBO> importLogs(int limit) {
        return importLogService.listRecent(limit);
    }
}
