package com.ruoyi.mapper.bill;

import com.ruoyi.bill.model.bo.TransactionsBO;
import com.ruoyi.bill.model.param.TransactionsParam;
import com.ruoyi.web.form.bill.TransactionsForm;
import com.ruoyi.web.vo.bill.TransactionsVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TransactionsConvert {

    TransactionsConvert INSTANCE = Mappers.getMapper(TransactionsConvert.class);

    List<TransactionsVO> toVOList(List<TransactionsBO> data);

    TransactionsVO toVO(TransactionsBO data);

    TransactionsParam toParam(TransactionsForm transactionsForm);

}