package com.ruoyi.user.convert;


import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.user.domain.CompanyBank;
import com.ruoyi.user.model.bo.CompanyBankBO;
import com.ruoyi.user.model.query.CompanyBankQuery;
import com.ruoyi.user.model.param.CompanyBankParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CompanyBankCov {

    CompanyBankCov INSTANCE = Mappers.getMapper(CompanyBankCov.class);


    List<CompanyBankBO>  listToBO(List<CompanyBank>  list );

    CompanyBankBO   toBO(CompanyBank  list );

    CompanyBank  queryToDomain(CompanyBankQuery query);

    CompanyBank  paramToDomain(CompanyBankParam param);
}

