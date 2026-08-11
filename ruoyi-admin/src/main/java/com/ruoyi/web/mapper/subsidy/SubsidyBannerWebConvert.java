package com.ruoyi.web.mapper.subsidy;

import com.ruoyi.subsidy.domain.GbBanner;
import com.ruoyi.subsidy.model.param.GbBannerParam;
import com.ruoyi.web.form.subsidy.SubsidyBannerForm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/** 国补轮播图 Web 转换器。 */
@Mapper
public interface SubsidyBannerWebConvert {
    SubsidyBannerWebConvert INSTANCE = Mappers.getMapper(SubsidyBannerWebConvert.class);
    /** 表单转领域参数。 */
    GbBannerParam toParam(SubsidyBannerForm form);
    /** 领域列表转响应。 */
    List<GbBanner> toVOList(List<GbBanner> source);
}
