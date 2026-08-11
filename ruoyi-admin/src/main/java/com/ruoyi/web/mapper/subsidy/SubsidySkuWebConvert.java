package com.ruoyi.web.mapper.subsidy;

import com.ruoyi.subsidy.model.bo.GbProductSkuBO;
import com.ruoyi.subsidy.model.param.GbProductSkuParam;
import com.ruoyi.web.form.subsidy.SubsidySkuForm;
import com.ruoyi.web.vo.subsidy.SubsidySkuVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/** 国补 SKU Web 转换器。 */
@Mapper
public interface SubsidySkuWebConvert {
    SubsidySkuWebConvert INSTANCE = Mappers.getMapper(SubsidySkuWebConvert.class);
    /** 表单转领域参数。 */
    GbProductSkuParam toParam(SubsidySkuForm form);
    /** 领域列表转响应。 */
    List<SubsidySkuVO> toVOList(List<GbProductSkuBO> source);
}
