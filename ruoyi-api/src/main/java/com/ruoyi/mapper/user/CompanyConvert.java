package com.ruoyi.mapper.user;

import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.web.vo.user.CompanyVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CompanyConvert {

    CompanyConvert INSTANCE = Mappers.getMapper(CompanyConvert.class);


    List<CompanyVO> toVoList(List<CompanyBO> companyBOS);


    CompanyVO toVo(CompanyBO companyBO);
}
