package com.ruoyi.biz.analysis;
import com.ruoyi.analysis.facade.AnalysisCollectionCycleConfigFacade;
import com.ruoyi.analysis.model.bo.AnalysisCollectionCycleConfigBO;
import com.ruoyi.analysis.model.param.AnalysisCollectionCycleConfigParam;
import com.ruoyi.analysis.model.query.AnalysisCollectionCycleConfigQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
/** 回款周期配置业务编排。 */
@Component
public class AnalysisCollectionCycleConfigBizService {
    @Autowired private AnalysisCollectionCycleConfigFacade collectionCycleConfigFacade;
    public List<AnalysisCollectionCycleConfigBO> list(AnalysisCollectionCycleConfigQuery query) { return collectionCycleConfigFacade.list(query); }
    public Long save(AnalysisCollectionCycleConfigParam param, Long operatorId) { param.setOperatorId(operatorId); return collectionCycleConfigFacade.save(param); }
}
