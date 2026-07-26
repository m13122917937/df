package com.ruoyi.master.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.master.domain.MasterSubjectBankTransaction;
import com.ruoyi.master.mapper.MasterSubjectBankTransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 主体银行卡资金流水明细业务层处理（原 TransactionsService）。
 *
 * @author ruoyi
 * @date 2025-12-01
 */
@Service
public class MasterSubjectBankTransactionService extends ServiceImpl<MasterSubjectBankTransactionMapper, MasterSubjectBankTransaction> {
    @Autowired
    private MasterSubjectBankTransactionMapper masterSubjectBankTransactionMapper;

}
