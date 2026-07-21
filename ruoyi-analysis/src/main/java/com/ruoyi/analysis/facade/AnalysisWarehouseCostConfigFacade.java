package com.ruoyi.analysis.facade;
import com.ruoyi.analysis.model.bo.AnalysisWarehouseCostConfigBO;
import com.ruoyi.analysis.model.param.AnalysisWarehouseCostConfigParam;
import com.ruoyi.analysis.model.query.AnalysisWarehouseCostConfigQuery;
import java.util.List;
/** 仓储售后成本对外接口。 */
public interface AnalysisWarehouseCostConfigFacade { List<AnalysisWarehouseCostConfigBO> list(AnalysisWarehouseCostConfigQuery query); Long save(AnalysisWarehouseCostConfigParam param); }
