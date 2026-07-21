package com.ruoyi.analysis.facade.impl;

import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisCollectionCycleConfig;
import com.ruoyi.analysis.facade.AnalysisCollectionCycleConfigFacade;
import com.ruoyi.analysis.model.bo.AnalysisCollectionCycleConfigBO;
import com.ruoyi.analysis.model.param.AnalysisCollectionCycleConfigParam;
import com.ruoyi.analysis.model.query.AnalysisCollectionCycleConfigQuery;
import com.ruoyi.analysis.service.AnalysisCollectionCycleConfigService;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.framework.mybatis.DynamicCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/** 回款周期配置领域接口实现。 */
@Component
public class AnalysisCollectionCycleConfigFacadeImpl implements AnalysisCollectionCycleConfigFacade {
    @Autowired private AnalysisCollectionCycleConfigService collectionCycleConfigService;
    @Override public List<AnalysisCollectionCycleConfigBO> list(AnalysisCollectionCycleConfigQuery query) {
        List<AnalysisCollectionCycleConfig> domains = collectionCycleConfigService.list(DynamicCondition.toWrapper(query, SortBy.of("-updated_time,-id")));
        return AnalysisConvert.INSTANCE.toCollectionCycleBOList(domains);
    }
    @Override public Long save(AnalysisCollectionCycleConfigParam param) { return collectionCycleConfigService.saveConfig(param); }
}
