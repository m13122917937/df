package com.ruoyi.analysis.convert;

import com.ruoyi.analysis.domain.AnalysisOrderFact;
import com.ruoyi.analysis.domain.AnalysisRefundFact;
import com.ruoyi.analysis.domain.AnalysisSyncLog;
import com.ruoyi.analysis.model.source.AnalysisOrderFactSource;
import com.ruoyi.analysis.model.source.AnalysisRefundFactSource;
import com.ruoyi.analysis.model.source.AnalysisSyncLogSource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 吉客云同步计算结果到经营事实实体的专用转换器。
 */
@Mapper
public interface AnalysisSyncConvert {
    AnalysisSyncConvert INSTANCE = Mappers.getMapper(AnalysisSyncConvert.class);

    /**
     * 转换订单商品行事实。
     */
    AnalysisOrderFact toDomain(AnalysisOrderFactSource source);

    /**
     * 转换退款商品行事实。
     */
    AnalysisRefundFact toDomain(AnalysisRefundFactSource source);

    /**
     * 转换同步日志快照。
     */
    AnalysisSyncLog toDomain(AnalysisSyncLogSource source);
}
