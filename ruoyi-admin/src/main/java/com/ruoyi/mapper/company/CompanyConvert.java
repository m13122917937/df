package com.ruoyi.mapper.company;


import com.github.pagehelper.Page;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.param.CompanyParam;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.web.form.company.CompanyAddForm;
import com.ruoyi.web.form.company.CompanyForm;
import com.ruoyi.web.vo.company.CompanyVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CompanyConvert {


    CompanyConvert INSTANCE = Mappers.getMapper(CompanyConvert.class);

    CompanyQuery toCompanyParam(CompanyForm companyForm);

    CompanyVO toVo(CompanyBO list);

    List<CompanyVO> toVoList(List<CompanyBO> list);

    CompanyParam toCompanyAddParam(CompanyAddForm companyForm);

    Page<CompanyVO> toVoListPage(List<CompanyBO> list);

    CompanyBO toParamToBO(CompanyParam companyParam);

}
