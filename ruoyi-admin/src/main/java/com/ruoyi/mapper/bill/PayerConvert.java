package com.ruoyi.mapper.bill;


import com.ruoyi.master.model.bo.MasterSubjectBankBO;
import com.ruoyi.master.model.param.MasterSubjectBankParam;
import com.ruoyi.web.form.bill.PayerForm;
import com.ruoyi.web.vo.bill.PayerVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PayerConvert {


    PayerConvert INSTANCE = Mappers.getMapper(PayerConvert.class);


    List<PayerVO> toVOList(List<MasterSubjectBankBO> data);

    MasterSubjectBankParam toParam(PayerForm payerForm);

}
