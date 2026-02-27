package com.ruoyi.bill.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.bill.domain.Bill;
import com.ruoyi.bill.mapper.BillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 账单Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-07
 */
@Service
public class BillManager  extends ServiceImpl<BillMapper, Bill> {
    @Autowired
    private BillMapper billMapper;


}
