package com.ruoyi.capital.facade;

import com.ruoyi.capital.model.bo.PaymentBO;
import com.ruoyi.capital.model.param.PaymentParam;
import com.ruoyi.capital.model.query.PaymentQuery;

public interface IPaymentFacade {

    PaymentBO queryOne(PaymentQuery paymentQuery);

    boolean update(PaymentParam paymentParam, PaymentQuery paymentQuery);

    PaymentBO save(PaymentParam paymentParam);

}
