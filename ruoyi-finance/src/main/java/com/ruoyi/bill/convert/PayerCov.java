package com.ruoyi.bill.convert;


import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.bill.domain.Payer;
import com.ruoyi.bill.model.bo.PayerBO;
import com.ruoyi.bill.model.query.PayerQuery;
import com.ruoyi.bill.model.param.PayerParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PayerCov {

    PayerCov INSTANCE = Mappers.getMapper(PayerCov.class);


    List<PayerBO>  listToBO(List<Payer>  list );

    PayerBO   toBO(Payer  list );

    Payer  queryToDomain(PayerQuery query);

    Payer  paramToDomain(PayerParam param);
}

