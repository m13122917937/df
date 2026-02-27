package com.ruoyi.mapper.company;


import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.user.domain.CompanyBank;
import com.ruoyi.user.model.bo.CompanyBankBO;
import com.ruoyi.user.model.param.CompanyBankParam;
import com.ruoyi.web.form.company.CompanyBankForm;
import com.ruoyi.web.vo.company.CompanyBankVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CompanyBankConvert {


    CompanyBankConvert INSTANCE = Mappers.getMapper(CompanyBankConvert.class);


    List<CompanyBankVO> toVOList(List<CompanyBankBO> list);


    CompanyBankParam toParam(CompanyBankForm companyBankForm);


}
