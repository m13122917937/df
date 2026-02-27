package com.ruoyi.express.manager;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.ruoyi.express.mapper.ExpressContrastMapper;
import com.ruoyi.express.domain.ExpressContrast;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-10-11
 */
@Service
public class ExpressContrastManager  extends ServiceImpl<ExpressContrastMapper, ExpressContrast> {
    @Autowired
    private ExpressContrastMapper expressContrastMapper;


}
