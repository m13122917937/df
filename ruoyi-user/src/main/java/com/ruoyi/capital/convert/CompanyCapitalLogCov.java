package com.ruoyi.capital.convert;

import com.ruoyi.capital.domain.CompanyCapitalLog;
import com.ruoyi.capital.model.bo.CompanyCapitalLogBO;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CompanyCapitalLogCov {

    CompanyCapitalLogCov INSTANCE = Mappers.getMapper(CompanyCapitalLogCov.class);


    CompanyCapitalLog toDomain(CompanyCapitalLogParam logParam);


    List<CompanyCapitalLogBO> listToBO(List<CompanyCapitalLog> companyCapitalLogs);


}

