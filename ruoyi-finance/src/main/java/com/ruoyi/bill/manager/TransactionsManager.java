package com.ruoyi.bill.manager;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.ruoyi.bill.mapper.TransactionsMapper;
import com.ruoyi.bill.domain.Transactions;

/**
 * 财务流水明细Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-01
 */
@Service
public class TransactionsManager  extends ServiceImpl<TransactionsMapper, Transactions> {
    @Autowired
    private TransactionsMapper transactionsMapper;


}
