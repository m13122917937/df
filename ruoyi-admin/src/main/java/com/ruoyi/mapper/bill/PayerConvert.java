package com.ruoyi.mapper.bill;


import com.ruoyi.bill.model.bo.PayerBO;
import com.ruoyi.bill.model.bo.PayerConfigBO;
import com.ruoyi.bill.model.param.PayerConfigParam;
import com.ruoyi.bill.model.param.PayerParam;
import com.ruoyi.web.form.bill.PayerForm;
import com.ruoyi.web.vo.bill.PayerVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PayerConvert {


    PayerConvert INSTANCE = Mappers.getMapper(PayerConvert.class);


    List<PayerVO> toVOList(List<PayerBO> data);

    PayerParam toParam(PayerForm payerForm);


    PayerConfigParam boToParam(PayerConfigBO payerConfigBO);
}
