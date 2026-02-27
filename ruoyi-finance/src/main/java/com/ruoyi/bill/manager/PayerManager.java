package com.ruoyi.bill.manager;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.ruoyi.bill.mapper.PayerMapper;
import com.ruoyi.bill.domain.Payer;

/**
 * 付款账号维护Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-07
 */
@Service
public class PayerManager  extends ServiceImpl<PayerMapper, Payer> {
    @Autowired
    private PayerMapper payerMapper;


}
