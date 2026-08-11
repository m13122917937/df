package com.ruoyi.web.mapper.subsidy;

import com.ruoyi.subsidy.model.bo.GbCategoryBO;
import com.ruoyi.subsidy.model.param.GbCategoryParam;
import com.ruoyi.web.form.subsidy.SubsidyCategoryForm;
import com.ruoyi.web.vo.subsidy.SubsidyCategoryVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/** 国补分类 Web 边界转换器。 */
@Mapper
public interface SubsidyCategoryWebConvert {
    SubsidyCategoryWebConvert INSTANCE = Mappers.getMapper(SubsidyCategoryWebConvert.class);
    /** 表单转领域参数。 */
    GbCategoryParam toParam(SubsidyCategoryForm form);
    /** 领域列表转响应。 */
    List<SubsidyCategoryVO> toVOList(List<GbCategoryBO> source);
}
