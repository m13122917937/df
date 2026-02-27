package com.ruoyi.order.manager;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.ruoyi.order.mapper.ImeiSkuRelMapper;
import com.ruoyi.order.domain.ImeiSkuRel;

/**
 * 型号映射决策Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-10-05
 */
@Service
public class ImeiSkuRelManager  extends ServiceImpl<ImeiSkuRelMapper, ImeiSkuRel> {
    @Autowired
    private ImeiSkuRelMapper imeiSkuRelMapper;


}
