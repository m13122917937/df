package com.ruoyi.capital.convert;

import com.ruoyi.capital.domain.CompanyCapital;
import com.ruoyi.capital.model.bo.CompanyCapitalBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CompanyCapitalCov {

    CompanyCapitalCov INSTANCE = Mappers.getMapper(CompanyCapitalCov.class);


    CompanyCapitalBO toBO(CompanyCapital one);
}

