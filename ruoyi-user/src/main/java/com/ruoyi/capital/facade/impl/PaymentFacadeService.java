package com.ruoyi.capital.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.capital.convert.PaymentCov;
import com.ruoyi.capital.domain.Payment;
import com.ruoyi.capital.facade.IPaymentFacade;
import com.ruoyi.capital.manager.PaymentManager;
import com.ruoyi.capital.model.bo.PaymentBO;
import com.ruoyi.capital.model.param.PaymentParam;
import com.ruoyi.capital.model.query.PaymentQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentFacadeService implements IPaymentFacade {

    @Autowired
    PaymentManager paymentManager;

    @Override
    public PaymentBO queryOne(final PaymentQuery paymentQuery) {
        Payment one = paymentManager.getOne(new QueryWrapper<>(PaymentCov.INSTANCE.toDomain(paymentQuery)));
        return PaymentCov.INSTANCE.toBO(one);
    }

    @Override
    public boolean update(final PaymentParam paymentParam, final PaymentQuery paymentQuery) {

        return paymentManager.update(PaymentCov.INSTANCE.paramToDomain(paymentParam), new QueryWrapper<>(PaymentCov.INSTANCE.toDomain(paymentQuery)));
    }

    @Override
    public PaymentBO save(final PaymentParam paymentParam) {
        Payment payment = PaymentCov.INSTANCE.paramToDomain(paymentParam);

        boolean save = paymentManager.save(payment);

        return PaymentCov.INSTANCE.toBO(payment);
    }
}
