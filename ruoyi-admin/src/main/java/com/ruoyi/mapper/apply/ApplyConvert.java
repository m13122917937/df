package com.ruoyi.mapper.apply;

import com.ruoyi.mapper.order.HangingOrderConvert;
import com.ruoyi.order.model.bo.ApplyBO;
import com.ruoyi.web.vo.apply.ApplyVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplyConvert {

    ApplyConvert INSTANCE = Mappers.getMapper(ApplyConvert.class);

    ApplyVO toVO(ApplyBO applyBO);

    
}
