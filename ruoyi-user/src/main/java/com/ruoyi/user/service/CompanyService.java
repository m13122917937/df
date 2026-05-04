package com.ruoyi.user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.user.domain.Company;
import com.ruoyi.user.mapper.CompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 企业Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-06
 */
@Service
public class CompanyService  extends ServiceImpl<CompanyMapper, Company> {
    @Autowired
    private CompanyMapper companyMapper;

}
