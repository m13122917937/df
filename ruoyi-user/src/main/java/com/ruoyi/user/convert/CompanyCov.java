package com.ruoyi.user.convert;


import com.github.pagehelper.Page;
import com.ruoyi.user.domain.Company;
import com.ruoyi.user.domain.MemberCompany;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.param.CompanyParam;
import com.ruoyi.user.model.param.MemberCompanyParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CompanyCov {

    CompanyCov INSTANCE = Mappers.getMapper(CompanyCov.class);

    // 添加单个对象转换方法
    CompanyBO toBO(Company company);

    List<CompanyBO> toBOList(List<Company> list);

    Company toDomain(CompanyParam companyParam);


    Page<CompanyBO> toBOPage(List<Company> list);

    Company paramToDomain(CompanyParam companyParam);


    MemberCompany paramToUserDomain(MemberCompanyParam memberCompanyParam);

}
