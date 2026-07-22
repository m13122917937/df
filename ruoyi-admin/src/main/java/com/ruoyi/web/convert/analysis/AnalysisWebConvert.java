package com.ruoyi.web.convert.analysis;

import com.ruoyi.analysis.model.bo.AnalysisCostConfigBO;
import com.ruoyi.analysis.model.bo.AnalysisCollectionCycleConfigBO;
import com.ruoyi.analysis.model.bo.AnalysisDashboardBO;
import com.ruoyi.analysis.model.bo.AnalysisImportLogBO;
import com.ruoyi.analysis.model.bo.AnalysisMarginConfigBO;
import com.ruoyi.analysis.model.bo.AnalysisOrderFactBO;
import com.ruoyi.analysis.model.bo.AnalysisSyncBO;
import com.ruoyi.analysis.model.bo.AnalysisWarehouseCostConfigBO;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.analysis.model.param.AnalysisCollectionCycleConfigParam;
import com.ruoyi.analysis.model.param.AnalysisMarginConfigParam;
import com.ruoyi.analysis.model.param.AnalysisWarehouseCostConfigParam;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import com.ruoyi.analysis.model.query.AnalysisCollectionCycleConfigQuery;
import com.ruoyi.analysis.model.query.AnalysisMarginConfigQuery;
import com.ruoyi.web.vo.analysis.AnalysisCostConfigSaveRequest;
import com.ruoyi.web.vo.analysis.AnalysisCollectionCycleConfigQueryRequest;
import com.ruoyi.web.vo.analysis.AnalysisCollectionCycleConfigSaveRequest;
import com.ruoyi.web.vo.analysis.AnalysisCollectionCycleConfigVO;
import com.ruoyi.web.vo.analysis.AnalysisCostConfigVO;
import com.ruoyi.web.vo.analysis.AnalysisDashboardVO;
import com.ruoyi.web.vo.analysis.AnalysisImportLogVO;
import com.ruoyi.web.vo.analysis.AnalysisMarginConfigQueryRequest;
import com.ruoyi.web.vo.analysis.AnalysisMarginConfigSaveRequest;
import com.ruoyi.web.vo.analysis.AnalysisMarginConfigVO;
import com.ruoyi.web.vo.analysis.AnalysisOrderFactVO;
import com.ruoyi.web.vo.analysis.AnalysisQueryRequest;
import com.ruoyi.web.vo.analysis.AnalysisSyncVO;
import com.ruoyi.web.vo.analysis.AnalysisWarehouseCostConfigSaveRequest;
import com.ruoyi.web.vo.analysis.AnalysisWarehouseCostConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 经营分析 Web 层与业务层对象转换器。
 */
@Mapper
public interface AnalysisWebConvert {
    AnalysisWebConvert INSTANCE = Mappers.getMapper(AnalysisWebConvert.class);

    AnalysisQuery toQuery(AnalysisQueryRequest source);

    AnalysisCostConfigParam toCostConfigParam(AnalysisCostConfigSaveRequest source);

    AnalysisCollectionCycleConfigQuery toCollectionCycleQuery(AnalysisCollectionCycleConfigQueryRequest source);

    AnalysisCollectionCycleConfigParam toCollectionCycleParam(AnalysisCollectionCycleConfigSaveRequest source);

    List<AnalysisCollectionCycleConfigVO> toCollectionCycleVOList(List<AnalysisCollectionCycleConfigBO> source);

    AnalysisMarginConfigQuery toMarginConfigQuery(AnalysisMarginConfigQueryRequest source);

    AnalysisMarginConfigParam toMarginConfigParam(AnalysisMarginConfigSaveRequest source);

    AnalysisWarehouseCostConfigParam toWarehouseCostParam(AnalysisWarehouseCostConfigSaveRequest source);

    List<AnalysisWarehouseCostConfigVO> toWarehouseCostVOList(List<AnalysisWarehouseCostConfigBO> source);

    AnalysisMarginConfigVO toMarginConfigVO(AnalysisMarginConfigBO source);

    List<AnalysisMarginConfigVO> toMarginConfigVOList(List<AnalysisMarginConfigBO> source);

    AnalysisDashboardVO toDashboardVO(AnalysisDashboardBO source);

    AnalysisDashboardVO.MetricVO toMetricVO(AnalysisDashboardBO.MetricBO source);

    AnalysisDashboardVO.DimensionMetricVO toDimensionMetricVO(AnalysisDashboardBO.DimensionMetricBO source);

    AnalysisDashboardVO.MetricNodeVO toMetricNodeVO(AnalysisDashboardBO.MetricNodeBO source);

    AnalysisOrderFactVO toOrderFactVO(AnalysisOrderFactBO source);

    List<AnalysisOrderFactVO> toOrderFactVOList(List<AnalysisOrderFactBO> source);

    AnalysisCostConfigVO toCostConfigVO(AnalysisCostConfigBO source);

    List<AnalysisCostConfigVO> toCostConfigVOList(List<AnalysisCostConfigBO> source);

    AnalysisSyncVO toSyncVO(AnalysisSyncBO source);

    AnalysisImportLogVO toImportLogVO(AnalysisImportLogBO source);

    List<AnalysisImportLogVO> toImportLogVOList(List<AnalysisImportLogBO> source);
}
