package com.ruoyi.analysis.convert;

import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.domain.AnalysisSyncLog;
import com.ruoyi.analysis.model.bo.AnalysisCostConfigBO;
import com.ruoyi.analysis.model.bo.AnalysisOrderFactBO;
import com.ruoyi.analysis.model.bo.AnalysisSyncBO;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import org.mapstruct.Mapper;
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
}
