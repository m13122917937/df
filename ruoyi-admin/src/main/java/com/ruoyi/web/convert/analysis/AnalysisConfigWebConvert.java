package com.ruoyi.web.convert.analysis;

import com.ruoyi.analysis.model.bo.AnalysisCostConfigBO;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.web.vo.analysis.AnalysisConfigExcelVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 核算配置 Web 层对象转换器。
 */
@Mapper
public interface AnalysisConfigWebConvert {
    AnalysisConfigWebConvert INSTANCE = Mappers.getMapper(AnalysisConfigWebConvert.class);

    /**
     * Excel 行转换为保存参数。
     */
    AnalysisCostConfigParam toParam(AnalysisConfigExcelVO source);

    /**
     * 业务对象转换为 Excel 行。
     */
    AnalysisConfigExcelVO toExcel(AnalysisCostConfigBO source);

    /**
     * 业务对象列表转换为 Excel 行。
     */
    List<AnalysisConfigExcelVO> toExcelList(List<AnalysisCostConfigBO> source);
}
