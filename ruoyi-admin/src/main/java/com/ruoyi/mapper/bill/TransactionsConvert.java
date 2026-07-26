package com.ruoyi.mapper.bill;

import com.ruoyi.master.model.bo.MasterSubjectBankTransactionBO;
import com.ruoyi.master.model.param.MasterSubjectBankTransactionParam;
import com.ruoyi.web.form.bill.TransactionsForm;
import com.ruoyi.web.vo.bill.TransactionsVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TransactionsConvert {

    TransactionsConvert INSTANCE = Mappers.getMapper(TransactionsConvert.class);

    List<TransactionsVO> toVOList(List<MasterSubjectBankTransactionBO> data);

    TransactionsVO toVO(MasterSubjectBankTransactionBO data);

    MasterSubjectBankTransactionParam toParam(TransactionsForm transactionsForm);

}