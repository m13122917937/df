package com.ruoyi.analysis.facade.impl;
import com.ruoyi.analysis.convert.AnalysisConvert;
import com.ruoyi.analysis.domain.AnalysisWarehouseCostConfig;
import com.ruoyi.analysis.facade.AnalysisWarehouseCostConfigFacade;
import com.ruoyi.analysis.model.bo.AnalysisWarehouseCostConfigBO;
import com.ruoyi.analysis.model.param.AnalysisWarehouseCostConfigParam;
import com.ruoyi.analysis.model.query.AnalysisWarehouseCostConfigQuery;
import com.ruoyi.analysis.service.AnalysisWarehouseCostConfigService;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.framework.mybatis.DynamicCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
/** 仓储售后成本接口实现。 */
@Component public class AnalysisWarehouseCostConfigFacadeImpl implements AnalysisWarehouseCostConfigFacade {
    @Autowired private AnalysisWarehouseCostConfigService warehouseCostConfigService;
    @Override public List<AnalysisWarehouseCostConfigBO> list(AnalysisWarehouseCostConfigQuery query) { List<AnalysisWarehouseCostConfig> rows = warehouseCostConfigService.list(DynamicCondition.toWrapper(query, SortBy.of("-month_value"))); return AnalysisConvert.INSTANCE.toWarehouseCostBOList(rows); }
    @Override public Long save(AnalysisWarehouseCostConfigParam param) { return warehouseCostConfigService.saveConfig(param); }
}
