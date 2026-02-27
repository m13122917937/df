package com.ruoyi.mapper.sys;


import com.ruoyi.system.model.bo.DictDistrictBO;
import com.ruoyi.web.vo.system.DictDistrictVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DictDistrictConvert {


    DictDistrictConvert INSTANCE = Mappers.getMapper(DictDistrictConvert.class);


    List<DictDistrictVO> toVo(List<DictDistrictBO> list);
}
