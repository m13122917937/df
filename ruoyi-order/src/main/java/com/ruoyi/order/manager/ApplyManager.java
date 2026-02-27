package com.ruoyi.order.manager;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.ruoyi.order.mapper.ApplyMapper;
import com.ruoyi.order.domain.Apply;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-23
 */
@Service
public class ApplyManager  extends ServiceImpl<ApplyMapper, Apply> {
    @Autowired
    private ApplyMapper applyMapper;


}
