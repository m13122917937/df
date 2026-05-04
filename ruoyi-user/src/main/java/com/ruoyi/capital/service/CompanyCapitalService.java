package com.ruoyi.capital.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.capital.domain.CompanyCapital;
import com.ruoyi.capital.mapper.CompanyCapitalMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Arith;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 企业押金Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-08
 */
@Slf4j
@Service
public class CompanyCapitalService extends ServiceImpl<CompanyCapitalMapper, CompanyCapital> {
    @Autowired
    private CompanyCapitalMapper companyCapitalMapper;

    /**
     * 查询初始化资金账号.
     *
     * @param serviceType 资金状态
     * @param companyId   企业ID
     */
    public CompanyCapital queryOrInit(Integer serviceType, Long companyId) {
        CompanyCapital companyCapital = this.getOne(new QueryWrapper<>(new CompanyCapital().setCompanyId(companyId).setServiceType(serviceType)));
        if (Objects.isNull(companyCapital)) {
            // 初始化资金账号
            companyCapital = this.initCompanyCapital(serviceType, companyId, BigDecimal.ZERO);
        }
        return companyCapital;
    }

    /**
     * 修改可用资金.
     *
     * @param companyCapital 资金信息
     * @param amount         扣除金额
     */
    public void updateAmount(CompanyCapital companyCapital, BigDecimal amount) {
        if (Objects.isNull(amount) || Objects.equals(BigDecimal.ZERO, amount)) {
            return;
        }
        // 判断可用资金是否充足
        if (!Arith.gte(companyCapital.getAvailableAmount(), BigDecimal.ZERO)
                && !Arith.gte(companyCapital.getAvailableAmount(), amount)) {
            throw new ServiceException("企业剩余可用金额不足");
        }
        if (!Arith.gte(Arith.add(companyCapital.getAvailableAmount(), amount), BigDecimal.ZERO)) {
            throw new ServiceException("企业剩余可用金额不足");
        }
        // 修改可用资金
        if (!this.getBaseMapper().updateAvailableAmount(companyCapital.getId(), amount)) {
            throw new ServiceException("系统繁忙，请重试！");
        }
        log.info("可用资金修改完成");
    }

    /**
     * 修改可用资金.
     *
     * @param companyCapital 资金信息
     * @param amount         扣除金额
     */
    public boolean updateFrozenAmount(CompanyCapital companyCapital, BigDecimal amount) {
        if (Objects.isNull(amount) || Objects.equals(BigDecimal.ZERO, amount)) {
            return false;
        }
        if (Arith.gt(amount, BigDecimal.ZERO)) {
            //冻结金额到 可用
            if (!Arith.gte(companyCapital.getFrozenAmount().abs(), BigDecimal.ZERO) && !Arith.gte(companyCapital.getFrozenAmount().abs(), amount)) {
                throw new ServiceException("企业剩余可用金额不足");
            }
            return this.baseMapper.updateFrozenAmount( companyCapital.getId(), amount );

        } else {
            //可用金额到 冻
            if (!Arith.gte(companyCapital.getAvailableAmount(), BigDecimal.ZERO) && !Arith.gte(companyCapital.getAvailableAmount(), amount.abs())) {
                throw new ServiceException("企业剩余可用金额不足");
            }
            return this.baseMapper.updateFrozenAmount( companyCapital.getId(), amount );

        }
    }

    public CompanyCapital initCompanyCapital(Integer serviceType, Long companyId, BigDecimal availableAmount) {
        DateTime date = DateUtil.date();
        CompanyCapital companyCapital = new CompanyCapital().setCompanyId(companyId)
                .setAvailableAmount(availableAmount).setFrozenAmount(BigDecimal.ZERO)
                .setCreateTime(date).setUpdateTime(date).setServiceType(serviceType);
        this.save(companyCapital);
        return companyCapital;
    }
}
