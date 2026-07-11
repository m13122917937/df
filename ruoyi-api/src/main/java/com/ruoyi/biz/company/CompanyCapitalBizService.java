package com.ruoyi.biz.company;

import com.ruoyi.common.utils.Arith;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class CompanyCapitalBizService {

    BigDecimal amount = new BigDecimal("20");

    BigDecimal rate = new BigDecimal("0.8");



    public BigDecimal calAmount(Integer quantity) {
        return Arith.mul(amount, new BigDecimal(quantity));
    }


    public BigDecimal calAmountRate(Integer quantity) {
        return Arith.mul(amount, new BigDecimal(quantity),  rate);
    }



}
