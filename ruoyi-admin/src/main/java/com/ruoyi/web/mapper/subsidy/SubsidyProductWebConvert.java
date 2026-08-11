package com.ruoyi.web.mapper.subsidy;

import com.ruoyi.subsidy.model.bo.GbProductBO;
import com.ruoyi.subsidy.model.param.GbProductParam;
import com.ruoyi.web.form.subsidy.SubsidyProductForm;
import com.ruoyi.web.vo.subsidy.SubsidyProductVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/** 国补商品 Web 边界转换器。 */
@Mapper
public interface SubsidyProductWebConvert {
    SubsidyProductWebConvert INSTANCE = Mappers.getMapper(SubsidyProductWebConvert.class);
    /** 表单转领域参数。 */
    GbProductParam toParam(SubsidyProductForm form);
    /** 领域对象转响应。 */
    SubsidyProductVO toVO(GbProductBO source);
    /** 领域列表转响应。 */
    List<SubsidyProductVO> toVOList(List<GbProductBO> source);
}
