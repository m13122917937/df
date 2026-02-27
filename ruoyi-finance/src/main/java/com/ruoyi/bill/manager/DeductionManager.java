package com.ruoyi.bill.manager;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.ruoyi.bill.mapper.DeductionMapper;
import com.ruoyi.bill.domain.Deduction;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-10
 */
@Service
public class DeductionManager  extends ServiceImpl<DeductionMapper, Deduction> {
    @Autowired
    private DeductionMapper deductionMapper;


}
