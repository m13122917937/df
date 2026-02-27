package com.ruoyi.mapper.product;

import com.ruoyi.order.model.bo.ApplyBO;
import com.ruoyi.web.vo.order.ApplyVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductConvert {

    ProductConvert INSTANCE = Mappers.getMapper(ProductConvert.class);



}
