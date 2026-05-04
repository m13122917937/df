package com.ruoyi.bill.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.bill.mapper.PayerConfigMapper;
import com.ruoyi.bill.domain.PayerConfig;

/**
 * 付款配置Service业务层处理
 *
 * @author ruoyi
 * @date 2025-11-07
 */
@Service
public class PayerConfigService  extends ServiceImpl<PayerConfigMapper, PayerConfig> {
    @Autowired
    private PayerConfigMapper payerConfigMapper;

}
