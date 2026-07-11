package com.ruoyi.order.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.order.domain.SalesReturn;
import com.ruoyi.order.mapper.SalesReturnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 销售退货单Service业务层处理
 *
 * @author ruoyi
 * @date 2026-06-30
 */
@Service
public class SalesReturnService extends ServiceImpl<SalesReturnMapper, SalesReturn> {
    @Autowired
    private SalesReturnMapper salesReturnMapper;
}
