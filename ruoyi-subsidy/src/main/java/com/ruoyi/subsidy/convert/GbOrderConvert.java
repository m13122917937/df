package com.ruoyi.subsidy.convert;

import com.ruoyi.subsidy.domain.GbOrder;
import com.ruoyi.subsidy.model.bo.GbOrderBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 国补订单对象转换器。
 */
@Mapper
public interface GbOrderConvert {
    GbOrderConvert INSTANCE = Mappers.getMapper(GbOrderConvert.class);

    GbOrderBO toBO(GbOrder source);
}
