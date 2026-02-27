package com.ruoyi.mapper.bill;

import com.ruoyi.bill.model.bo.PayerConfigBO;
import com.ruoyi.bill.model.param.PayerConfigParam;
import com.ruoyi.web.form.bill.PayerConfigForm;
import com.ruoyi.web.vo.bill.PayerConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PayerConfigConvert {


    PayerConfigConvert INSTANCE = Mappers.getMapper(PayerConfigConvert.class);

    List<PayerConfigVO> toVOList(List<PayerConfigBO> data);

    PayerConfigParam toParam(PayerConfigForm payerConfigForm);

}
