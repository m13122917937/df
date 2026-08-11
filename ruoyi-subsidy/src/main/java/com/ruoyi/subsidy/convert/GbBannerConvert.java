package com.ruoyi.subsidy.convert;

import com.ruoyi.subsidy.domain.GbBanner;
import com.ruoyi.subsidy.model.param.GbBannerParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/** 国补轮播图领域转换器。 */
@Mapper
public interface GbBannerConvert {
    GbBannerConvert INSTANCE = Mappers.getMapper(GbBannerConvert.class);
    /** 参数转持久化实体。 */
    GbBanner toEntity(GbBannerParam param);
}
