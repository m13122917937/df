package com.ruoyi.analysis.facade;

import com.ruoyi.analysis.model.bo.AnalysisCollectionCycleConfigBO;
import com.ruoyi.analysis.model.param.AnalysisCollectionCycleConfigParam;
import com.ruoyi.analysis.model.query.AnalysisCollectionCycleConfigQuery;
import java.util.List;

/** 回款周期配置领域对外接口。 */
public interface AnalysisCollectionCycleConfigFacade {
    List<AnalysisCollectionCycleConfigBO> list(AnalysisCollectionCycleConfigQuery query);
    Long save(AnalysisCollectionCycleConfigParam param);
}
