package com.ruoyi.mapper.order;

import com.ruoyi.order.model.bo.CompanyOrderBO;
import com.ruoyi.web.vo.order.WarehousingOrderVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface WarehousingConvert {

    WarehousingConvert INSTANCE = Mappers.getMapper(WarehousingConvert.class);


    List<WarehousingOrderVO> toVO(List<CompanyOrderBO> data);

    
}
