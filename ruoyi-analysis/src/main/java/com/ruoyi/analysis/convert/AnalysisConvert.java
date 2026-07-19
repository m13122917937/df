package com.ruoyi.analysis.convert;

import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.domain.AnalysisRebateActivity;
import com.ruoyi.analysis.domain.AnalysisRebateDetail;
import com.ruoyi.analysis.domain.AnalysisSyncLog;
import com.ruoyi.analysis.model.bo.AnalysisCostConfigBO;
import com.ruoyi.analysis.model.bo.AnalysisOrderFactBO;
import com.ruoyi.analysis.model.bo.AnalysisRebateBO;
import com.ruoyi.analysis.model.bo.AnalysisSyncBO;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.analysis.model.param.AnalysisRebateParam;
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
