package com.ruoyi.master.convert;


import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.master.domain.MasterSubjectBankTransaction;
import com.ruoyi.master.model.bo.MasterSubjectBankTransactionBO;
import com.ruoyi.master.model.query.MasterSubjectBankTransactionQuery;
import com.ruoyi.master.model.param.MasterSubjectBankTransactionParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MasterSubjectBankTransactionConvert {

    MasterSubjectBankTransactionConvert INSTANCE = Mappers.getMapper(MasterSubjectBankTransactionConvert.class);


    List<MasterSubjectBankTransactionBO> listToBO(List<MasterSubjectBankTransaction> list);

    MasterSubjectBankTransactionBO toBO(MasterSubjectBankTransaction list);

    MasterSubjectBankTransaction queryToDomain(MasterSubjectBankTransactionQuery query);

    MasterSubjectBankTransaction paramToDomain(MasterSubjectBankTransactionParam param);

    MasterSubjectBankTransaction toEntity(MasterSubjectBankTransactionParam param);

}
