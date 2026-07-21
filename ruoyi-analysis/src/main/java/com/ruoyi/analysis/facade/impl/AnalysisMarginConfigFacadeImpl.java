package com.ruoyi.analysis.facade.impl;

import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisMarginConfig;
import com.ruoyi.analysis.facade.AnalysisMarginConfigFacade;
import com.ruoyi.analysis.model.bo.AnalysisMarginConfigBO;
import com.ruoyi.analysis.model.param.AnalysisMarginConfigParam;
import com.ruoyi.analysis.model.query.AnalysisMarginConfigQuery;
import com.ruoyi.analysis.service.AnalysisMarginConfigService;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.framework.mybatis.DynamicCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 保证金配置领域接口实现。
 */
@Component
public class AnalysisMarginConfigFacadeImpl implements AnalysisMarginConfigFacade {
    @Autowired
    private AnalysisMarginConfigService marginConfigService;

    @Override
    public List<AnalysisMarginConfigBO> list(AnalysisMarginConfigQuery query) {
        List<AnalysisMarginConfig> domains = marginConfigService.list(DynamicCondition.toWrapper(
                query, SortBy.of("-updated_time,-id")));
        return AnalysisConvert.INSTANCE.toMarginBOList(domains);
    }

    @Override
    public Long save(AnalysisMarginConfigParam param) {
        return marginConfigService.saveConfig(param);
    }

    @Override
    public void delete(Long id) {
        marginConfigService.deleteConfig(id);
    }
}
