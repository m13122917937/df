package com.ruoyi.biz.analysis;
import com.ruoyi.analysis.facade.AnalysisWarehouseCostConfigFacade;
import com.ruoyi.analysis.model.bo.AnalysisWarehouseCostConfigBO;
import com.ruoyi.analysis.model.param.AnalysisWarehouseCostConfigParam;
import com.ruoyi.analysis.model.query.AnalysisWarehouseCostConfigQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
@Component public class AnalysisWarehouseCostConfigBizService {
    @Autowired private AnalysisWarehouseCostConfigFacade warehouseCostConfigFacade;
    public List<AnalysisWarehouseCostConfigBO> list(AnalysisWarehouseCostConfigQuery query) { return warehouseCostConfigFacade.list(query); }
    public Long save(AnalysisWarehouseCostConfigParam param, Long operatorId) { param.setOperatorId(operatorId); return warehouseCostConfigFacade.save(param); }
}
