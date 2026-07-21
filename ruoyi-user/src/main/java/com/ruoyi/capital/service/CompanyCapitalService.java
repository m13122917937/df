package com.ruoyi.capital.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.capital.convert.CompanyCapitalLogCov;
import com.ruoyi.capital.domain.CompanyCapital;
import com.ruoyi.capital.domain.CompanyCapitalLog;
import com.ruoyi.capital.mapper.CompanyCapitalMapper;
import com.ruoyi.capital.model.consts.CompanyCapitalConsts;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.capital.model.query.CompanyCapitalQuery;
import com.ruoyi.capital.model.query.CompanyCapitalLogQuery;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Arith;
import com.ruoyi.framework.mybatis.DynamicCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor
public class CompanyCapitalService extends ServiceImpl<CompanyCapitalMapper, CompanyCapital> {
    private final CompanyCapitalLogService companyCapitalLogService;

    /**
     * 修改企业可用资金并记录流水。
     *
     * @param logParam 资金流水参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeAvailable(CompanyCapitalLogParam logParam) {
        CompanyCapital capital = queryOrInit(CompanyCapitalConsts.Types.DEPOSIT.getCode(), logParam.getCompanyId());
        logParam.setCapitalId(capital.getId()).setCompanyId(capital.getCompanyId()).setCreateTime(DateUtil.date());
        logParam.setSubtotal(Arith.add(logParam.getSubtotal(), logParam.getAddAmount()))
                .setAvailableAmount(Arith.add(capital.getAvailableAmount(), logParam.getSubtotal()));
        if (!companyCapitalLogService.save(CompanyCapitalLogCov.INSTANCE.toDomain(logParam))) {
            return;
        }
        updateAmount(capital, logParam.getAddAmount());
    }

    /**
     * 冻结企业资金并记录流水。
     *
     * @param logParam 资金流水参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void freeze(CompanyCapitalLogParam logParam) {
        validateBusinessKey(logParam);
        CompanyCapital capital = queryOrInit(CompanyCapitalConsts.Types.DEPOSIT.getCode(), logParam.getCompanyId());
        CompanyCapitalLog existing = findCapitalLog(capital.getId(), logParam);
        if (Objects.nonNull(existing)) {
            return;
        }
        CompanyCapitalLog capitalLog = CompanyCapitalLogCov.INSTANCE.toDomain(logParam)
                .setCapitalId(capital.getId()).setCreateTime(DateUtil.date())
                .setSubtotal(Arith.add(logParam.getAddAmount(), logParam.getOutAmount()));
        capitalLog.setAddAmount(Objects.requireNonNullElse(logParam.getAddAmount(), BigDecimal.ZERO))
                .setSubtotal(Objects.requireNonNullElse(logParam.getSubtotal(), BigDecimal.ZERO));
        capitalLog.setAvailableAmount(Arith.add(capital.getAvailableAmount(),
                Arith.add(logParam.getAddAmount(), logParam.getOutAmount())));
        companyCapitalLogService.save(capitalLog);
        if (!updateFrozenAmount(capital, Arith.add(logParam.getAddAmount(), logParam.getOutAmount()))) {
            throw new ServiceException("修改资金账号失败", HttpStatus.CAPITAL_WARN);
        }
    }

    /**
     * 解冻企业资金并更新流水。
     *
     * @param logParam 资金流水参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void unFreeze(CompanyCapitalLogParam logParam) {
        validateBusinessKey(logParam);
        CompanyCapital capital = queryOrInit(CompanyCapitalConsts.Types.DEPOSIT.getCode(), logParam.getCompanyId());
        CompanyCapitalLog capitalLog = findCapitalLog(capital.getId(), logParam);
        if (Objects.isNull(capitalLog) || isRepeatedAmount(capitalLog, logParam)) {
            return;
        }
        BigDecimal addAmount = Arith.add(capitalLog.getAddAmount(), logParam.getAddAmount());
        BigDecimal outAmount = Arith.add(capitalLog.getOutAmount(), logParam.getOutAmount());
        if (Arith.gt(Arith.sub(addAmount, outAmount.abs()), BigDecimal.ZERO)) {
            log.info("业务单号：{}，金额不一致，请检查, addAmount:{}, outAmount:{}",
                    logParam.getOrderNo(), addAmount, outAmount);
            return;
        }
        updateCapitalLog(capital, capitalLog, logParam);
        if (!updateFrozenAmount(capital, Arith.add(logParam.getAddAmount(), logParam.getOutAmount()))) {
            throw new ServiceException("修改资金账号失败", HttpStatus.CAPITAL_WARN);
        }
    }

    private void validateBusinessKey(CompanyCapitalLogParam logParam) {
        Assert.notBlank(logParam.getOrderNo(), "业务单号不能为空");
        Assert.notNull(logParam.getCompanyId(), "企业ID不能为空");
    }

    private CompanyCapitalLog findCapitalLog(Long capitalId, CompanyCapitalLogParam logParam) {
        CompanyCapitalLogQuery query = new CompanyCapitalLogQuery().setCapitalId(capitalId)
                .setOrderNo(logParam.getOrderNo()).setType(logParam.getType()).setTradeId(logParam.getTradeId());
        return companyCapitalLogService.getOne(DynamicCondition.toWrapper(query));
    }

    private boolean isRepeatedAmount(CompanyCapitalLog capitalLog, CompanyCapitalLogParam logParam) {
        BigDecimal outAmount = Objects.requireNonNullElse(logParam.getOutAmount(), BigDecimal.ZERO);
        BigDecimal addAmount = Objects.requireNonNullElse(logParam.getAddAmount(), BigDecimal.ZERO);
        return Arith.eq(capitalLog.getOutAmount(), outAmount) && Arith.eq(capitalLog.getAddAmount(), addAmount);
    }

    private void updateCapitalLog(CompanyCapital capital, CompanyCapitalLog capitalLog,
                                  CompanyCapitalLogParam logParam) {
        capitalLog.setAddAmount(Arith.add(capitalLog.getAddAmount(), logParam.getAddAmount()))
                .setOutAmount(Arith.add(capitalLog.getOutAmount(), logParam.getOutAmount()))
                .setAvailableAmount(Arith.add(capital.getAvailableAmount(), capitalLog.getAddAmount()))
                .setUpdateTime(DateUtil.date())
                .setSubtotal(Arith.add(capitalLog.getAddAmount(), capitalLog.getOutAmount()));
        companyCapitalLogService.update(capitalLog,
                DynamicCondition.toWrapper(new CompanyCapitalLogQuery().setId(capitalLog.getId())));
    }

    /**
     * 查询初始化资金账号.
     *
     * @param serviceType 资金状态
     * @param companyId   企业ID
     */
    public CompanyCapital queryOrInit(Integer serviceType, Long companyId) {
        CompanyCapitalQuery query = new CompanyCapitalQuery().setCompanyId(companyId).setServiceType(serviceType);
        CompanyCapital companyCapital = getOne(DynamicCondition.toWrapper(query));
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
