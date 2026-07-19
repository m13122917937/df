package com.ruoyi.analysis.convert;

import com.ruoyi.analysis.model.query.AnalysisCostConfigQuery;
import com.ruoyi.analysis.model.query.AnalysisFactQuery;
import com.ruoyi.analysis.model.query.AnalysisMetricQuery;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 统一 Web 查询条件到各领域实体查询条件的转换器。
 */
@Mapper
public interface AnalysisQueryConvert {
    AnalysisQueryConvert INSTANCE = Mappers.getMapper(AnalysisQueryConvert.class);

    AnalysisFactQuery toFactQuery(AnalysisQuery query);

    @Mapping(target = "calcStatus", constant = "INCOMPLETE")
    AnalysisFactQuery toIncompleteFactQuery(AnalysisQuery query);

    AnalysisMetricQuery toMetricQuery(AnalysisQuery query);

    AnalysisCostConfigQuery toCostConfigQuery(AnalysisQuery query);
}
