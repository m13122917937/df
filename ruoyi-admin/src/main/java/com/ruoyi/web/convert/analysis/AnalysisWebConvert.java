package com.ruoyi.web.convert.analysis;

import com.ruoyi.analysis.model.bo.AnalysisCostConfigBO;
import com.ruoyi.analysis.model.bo.AnalysisDashboardBO;
import com.ruoyi.analysis.model.bo.AnalysisOrderFactBO;
import com.ruoyi.analysis.model.bo.AnalysisRebateBO;
import com.ruoyi.analysis.model.bo.AnalysisSyncBO;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.analysis.model.param.AnalysisRebateParam;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import com.ruoyi.web.vo.analysis.AnalysisCostConfigSaveRequest;
import com.ruoyi.web.vo.analysis.AnalysisCostConfigVO;
import com.ruoyi.web.vo.analysis.AnalysisDashboardVO;
import com.ruoyi.web.vo.analysis.AnalysisOrderFactVO;
import com.ruoyi.web.vo.analysis.AnalysisQueryRequest;
import com.ruoyi.web.vo.analysis.AnalysisRebateSaveRequest;
import com.ruoyi.web.vo.analysis.AnalysisRebateVO;
import com.ruoyi.web.vo.analysis.AnalysisSyncVO;
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

    AnalysisRebateParam toRebateParam(AnalysisRebateSaveRequest source);

    AnalysisRebateParam.DetailParam toRebateDetailParam(AnalysisRebateSaveRequest.DetailRequest source);

    AnalysisDashboardVO toDashboardVO(AnalysisDashboardBO source);

    AnalysisDashboardVO.MetricVO toMetricVO(AnalysisDashboardBO.MetricBO source);

    AnalysisDashboardVO.DimensionMetricVO toDimensionMetricVO(AnalysisDashboardBO.DimensionMetricBO source);

    AnalysisDashboardVO.MetricNodeVO toMetricNodeVO(AnalysisDashboardBO.MetricNodeBO source);

    AnalysisOrderFactVO toOrderFactVO(AnalysisOrderFactBO source);

    List<AnalysisOrderFactVO> toOrderFactVOList(List<AnalysisOrderFactBO> source);

    AnalysisCostConfigVO toCostConfigVO(AnalysisCostConfigBO source);

    List<AnalysisCostConfigVO> toCostConfigVOList(List<AnalysisCostConfigBO> source);

    AnalysisSyncVO toSyncVO(AnalysisSyncBO source);

    List<AnalysisSyncVO> toSyncVOList(List<AnalysisSyncBO> source);

    AnalysisRebateVO toRebateVO(AnalysisRebateBO source);

    List<AnalysisRebateVO> toRebateVOList(List<AnalysisRebateBO> source);
}
