package com.ruoyi.capital.convert;

import com.ruoyi.capital.domain.Payment;
import com.ruoyi.capital.model.bo.PaymentBO;
import com.ruoyi.capital.model.param.PaymentParam;
import com.ruoyi.capital.model.query.PaymentQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentCov {

    PaymentCov INSTANCE = Mappers.getMapper(PaymentCov.class);


    Payment toDomain(PaymentQuery paymentQuery);


    PaymentBO toBO(Payment one);

    Payment paramToDomain(PaymentParam paymentParam);

}

