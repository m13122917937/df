package com.ruoyi.order.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.order.domain.Imei;
import com.ruoyi.order.mapper.ImeiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 订单串码Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-11
 */
@Service
public class ImeiManager  extends ServiceImpl<ImeiMapper, Imei> {
    @Autowired
    private ImeiMapper imeiMapper;


}
