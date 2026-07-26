package com.ruoyi.master.convert;


import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.master.domain.MasterSubjectBank;
import com.ruoyi.master.model.bo.MasterSubjectBankBO;
import com.ruoyi.master.model.query.MasterSubjectBankQuery;
import com.ruoyi.master.model.param.MasterSubjectBankParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MasterSubjectBankConvert {

    MasterSubjectBankConvert INSTANCE = Mappers.getMapper(MasterSubjectBankConvert.class);


    List<MasterSubjectBankBO> listToBO(List<MasterSubjectBank> list);

    MasterSubjectBankBO toBO(MasterSubjectBank list);

    MasterSubjectBank queryToDomain(MasterSubjectBankQuery query);

    MasterSubjectBank paramToDomain(MasterSubjectBankParam param);
}
