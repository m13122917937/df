package com.ruoyi.capital.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.capital.domain.Recharge;
import com.ruoyi.capital.mapper.RechargeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 企业充值记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-08
 */
@Service
public class RechargeManager extends ServiceImpl<RechargeMapper, Recharge> {
    @Autowired
    private RechargeMapper rechargeMapper;

}
