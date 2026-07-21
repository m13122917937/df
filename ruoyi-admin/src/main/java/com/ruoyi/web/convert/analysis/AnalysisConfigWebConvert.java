package com.ruoyi.web.convert.analysis;

import com.ruoyi.analysis.model.bo.AnalysisCostConfigBO;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.web.vo.analysis.AnalysisConfigExcelVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
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
    @Mapping(target = "extraData", ignore = true)
    AnalysisCostConfigParam toParam(AnalysisConfigExcelVO source);

    /**
     * 业务对象转换为 Excel 行。
     */
    AnalysisConfigExcelVO toExcel(AnalysisCostConfigBO source);

    /**
     * 业务对象列表转换为 Excel 行。
     */
    List<AnalysisConfigExcelVO> toExcelList(List<AnalysisCostConfigBO> source);

    /**
     * 组装导入模板中的资金与人力扩展字段。
     *
     * @param source Excel 行
     * @param target 保存参数
     */
    @AfterMapping
    default void fillExtraData(AnalysisConfigExcelVO source, @MappingTarget AnalysisCostConfigParam target) {
        ObjectNode extra = JsonNodeFactory.instance.objectNode();
        if (source.getAnnualRatePercent() != null) {
            extra.put("annualRate", source.getAnnualRatePercent().divide(BigDecimal.valueOf(100)));
        }
        if (source.getCostScope() != null && !source.getCostScope().trim().isEmpty()) {
            extra.put("costScope", source.getCostScope().trim());
        }
        if (source.getHeadcount() != null) {
            extra.put("headcount", source.getHeadcount());
        }
        target.setExtraData(extra.size() == 0 ? null : JacksonUtil.toJson(extra));
    }

    /**
     * 展开导出模板中的资金与人力扩展字段。
     *
     * @param source 配置业务对象
     * @param target Excel 行
     */
    @AfterMapping
    default void fillExcelBusinessFields(AnalysisCostConfigBO source, @MappingTarget AnalysisConfigExcelVO target) {
        if (source.getExtraData() == null || source.getExtraData().trim().isEmpty()) {
            return;
        }
        JsonNode extra = JacksonUtil.readTree(source.getExtraData());
        if (extra.hasNonNull("annualRate")) {
            target.setAnnualRatePercent(extra.get("annualRate").decimalValue().multiply(BigDecimal.valueOf(100)));
        }
        if (extra.hasNonNull("costScope")) {
            target.setCostScope(extra.get("costScope").asText());
        }
        if (extra.hasNonNull("headcount")) {
            target.setHeadcount(extra.get("headcount").decimalValue());
        }
    }
}
