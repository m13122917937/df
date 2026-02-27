package com.ruoyi.capital.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.capital.domain.Payment;
import com.ruoyi.capital.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 企业支付记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-08
 */
@Service
public class PaymentManager  extends ServiceImpl<PaymentMapper, Payment> {
    @Autowired
    private PaymentMapper paymentMapper;


}
