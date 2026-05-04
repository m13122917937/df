package com.ruoyi.bill.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.bill.mapper.BillPayPlanMapper;
import com.ruoyi.bill.domain.BillPayPlan;

/**
 * 排款计划Service业务层处理
 *
 * @author ruoyi
 * @date 2025-11-09
 */
@Service
public class BillPayPlanService  extends ServiceImpl<BillPayPlanMapper, BillPayPlan> {
    @Autowired
    private BillPayPlanMapper billPayPlanMapper;

}
