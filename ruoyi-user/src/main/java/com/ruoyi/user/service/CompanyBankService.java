package com.ruoyi.user.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.user.mapper.CompanyBankMapper;
import com.ruoyi.user.domain.CompanyBank;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-28
 */
@Service
public class CompanyBankService  extends ServiceImpl<CompanyBankMapper, CompanyBank> {
    @Autowired
    private CompanyBankMapper companyBankMapper;

}
