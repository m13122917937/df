package com.ruoyi.mapper.order;

import com.ruoyi.order.model.bo.ApplyBO;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.web.vo.order.ApplyVO;
import com.ruoyi.web.vo.order.ImeiVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ApplyConvert {

    ApplyConvert INSTANCE = Mappers.getMapper(ApplyConvert.class);


    ApplyVO toVO(ApplyBO applyBO);

}
