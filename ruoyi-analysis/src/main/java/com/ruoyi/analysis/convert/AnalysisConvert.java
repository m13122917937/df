package com.ruoyi.analysis.convert;

import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.domain.AnalysisCollectionCycleConfig;
import com.ruoyi.analysis.domain.AnalysisImportLog;
import com.ruoyi.analysis.domain.AnalysisMarginConfig;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.domain.AnalysisRebateActivity;
import com.ruoyi.analysis.domain.AnalysisRebateDetail;
import com.ruoyi.analysis.domain.AnalysisSyncLog;
import com.ruoyi.analysis.domain.AnalysisWarehouseCostConfig;
import com.ruoyi.analysis.model.bo.AnalysisCostConfigBO;
import com.ruoyi.analysis.model.bo.AnalysisCollectionCycleConfigBO;
import com.ruoyi.analysis.model.bo.AnalysisImportLogBO;
import com.ruoyi.analysis.model.bo.AnalysisMarginConfigBO;
import com.ruoyi.analysis.model.bo.AnalysisOrderFactBO;
import com.ruoyi.analysis.model.bo.AnalysisRebateBO;
import com.ruoyi.analysis.model.bo.AnalysisSyncBO;
import com.ruoyi.analysis.model.bo.AnalysisWarehouseCostConfigBO;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.analysis.model.param.AnalysisCollectionCycleConfigParam;
import com.ruoyi.analysis.model.param.AnalysisMarginConfigParam;
import com.ruoyi.analysis.model.param.AnalysisRebateParam;
import com.ruoyi.analysis.model.param.AnalysisWarehouseCostConfigParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 经营分析层间对象转换器。
 */
@Mapper
public interface AnalysisConvert {
    AnalysisConvert INSTANCE = Mappers.getMapper(AnalysisConvert.class);

    AnalysisCostConfig toDomain(AnalysisCostConfigParam param);

    AnalysisCostConfigBO toBO(AnalysisCostConfig domain);

    List<AnalysisCostConfigBO> toConfigBOList(List<AnalysisCostConfig> domains);

    AnalysisCollectionCycleConfig toDomain(AnalysisCollectionCycleConfigParam param);

    List<AnalysisCollectionCycleConfigBO> toCollectionCycleBOList(List<AnalysisCollectionCycleConfig> domains);

    AnalysisMarginConfig toDomain(AnalysisMarginConfigParam param);

    AnalysisMarginConfigBO toMarginBO(AnalysisMarginConfig domain);

    List<AnalysisMarginConfigBO> toMarginBOList(List<AnalysisMarginConfig> domains);

    AnalysisWarehouseCostConfig toDomain(AnalysisWarehouseCostConfigParam param);

    List<AnalysisWarehouseCostConfigBO> toWarehouseCostBOList(List<AnalysisWarehouseCostConfig> domains);

    AnalysisImportLogBO toBO(AnalysisImportLog domain);

    List<AnalysisImportLogBO> toImportLogBOList(List<AnalysisImportLog> domains);

    AnalysisOrderFactBO toBO(AnalysisOrderFact domain);

    List<AnalysisOrderFactBO> toFactBOList(List<AnalysisOrderFact> domains);

    AnalysisSyncBO toBO(AnalysisSyncLog domain);

    List<AnalysisSyncBO> toSyncBOList(List<AnalysisSyncLog> domains);

    AnalysisRebateActivity toDomain(AnalysisRebateParam param);

    AnalysisRebateDetail toDomain(AnalysisRebateParam.DetailParam param);

    List<AnalysisRebateDetail> toRebateDetailList(List<AnalysisRebateParam.DetailParam> params);

    AnalysisRebateBO toBO(AnalysisRebateActivity domain);

    @Mapping(target = "details", source = "details")
    AnalysisRebateBO toBO(AnalysisRebateActivity domain, List<AnalysisRebateDetail> details);

    AnalysisRebateBO.DetailBO toBO(AnalysisRebateDetail domain);

    List<AnalysisRebateBO.DetailBO> toRebateDetailBOList(List<AnalysisRebateDetail> domains);
}
