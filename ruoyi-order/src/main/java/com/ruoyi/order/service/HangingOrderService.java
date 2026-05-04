package com.ruoyi.order.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.order.domain.HangingOrder;
import com.ruoyi.order.mapper.HangingOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 挂单Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Service
public class HangingOrderService  extends ServiceImpl<HangingOrderMapper, HangingOrder> {
    @Autowired
    private HangingOrderMapper hangingOrderMapper;

}
