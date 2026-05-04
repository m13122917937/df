package com.ruoyi.capital.facade.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.capital.convert.CompanyCapitalCov;
import com.ruoyi.capital.convert.CompanyCapitalLogCov;
import com.ruoyi.capital.domain.CompanyCapital;
import com.ruoyi.capital.domain.CompanyCapitalLog;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.capital.service.CompanyCapitalService;
import com.ruoyi.capital.service.CompanyCapitalLogService;
import com.ruoyi.capital.model.bo.CompanyCapitalBO;
import com.ruoyi.capital.model.bo.CompanyCapitalLogBO;
import com.ruoyi.capital.model.consts.CompanyCapitalConsts;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.capital.model.query.CompanyCapitalLogQuery;
import com.ruoyi.capital.model.query.CompanyCapitalQuery;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.Arith;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CompanyCapitalFacade implements ICompanyCapitalFacade {

    @Autowired
    CompanyCapitalService companyCapitalService;


    @Autowired
    CompanyCapitalLogService companyCapitalLogService;


    @Override
    public CompanyCapitalBO queryOne(CompanyCapitalQuery query) {

        CompanyCapital companyCapital = companyCapitalService.getOne(DynamicCondition.toWrapper(query));

        return CompanyCapitalCov.INSTANCE.toBO(companyCapital);
    }

    @Override
    @Transactional
    public void changeAvailable(final CompanyCapitalLogParam logParam) {
        CompanyCapital companyCapital = companyCapitalService.queryOrInit(CompanyCapitalConsts.Types.DEPOSIT.getCode(), logParam.getCompanyId());
        // 增加资金记录
        logParam.setCapitalId(companyCapital.getId()).setCompanyId(companyCapital.getCompanyId()).setCreateTime(DateUtil.date());
        logParam.setSubtotal(Arith.add(logParam.getSubtotal(), logParam.getAddAmount())).setAvailableAmount(Arith.add(companyCapital.getAvailableAmount(), logParam.getSubtotal()));
        if (!companyCapitalLogService.save(CompanyCapitalLogCov.INSTANCE.toDomain(logParam))) {
            return;
        }
        // 修改资金账号
        companyCapitalService.updateAmount(companyCapital, logParam.getAddAmount());
    }

    @Override
    @Transactional
    public void freeze(CompanyCapitalLogParam logParam) {
        Assert.notBlank(logParam.getOrderNo(), "业务单号不能为空");
        Assert.notNull(logParam.getCompanyId(), "企业ID不能为空");

        CompanyCapital companyCapital = companyCapitalService.queryOrInit(CompanyCapitalConsts.Types.DEPOSIT.getCode(), logParam.getCompanyId());

        CompanyCapitalLog companyCapitalLog = companyCapitalLogService.getOne(DynamicCondition.toWrapper(new CompanyCapitalLogQuery()
                .setCapitalId(companyCapital.getId()).setOrderNo(logParam.getOrderNo()).setType(logParam.getType()).setTradeId(logParam.getTradeId())));
        if (Objects.nonNull(companyCapitalLog)) {
            return;
        }
        // 修改资金账号
        companyCapitalLog = CompanyCapitalLogCov.INSTANCE.toDomain(logParam).setCapitalId(companyCapital.getId()).setCreateTime(DateUtil.date()).setSubtotal(Arith.add(logParam.getAddAmount(), logParam.getOutAmount()));
        companyCapitalLog.setAddAmount(Objects.requireNonNullElse(logParam.getAddAmount(), BigDecimal.ZERO)).setSubtotal(Objects.requireNonNullElse(logParam.getSubtotal(), BigDecimal.ZERO));
        companyCapitalLog.setAvailableAmount(Arith.add(companyCapital.getAvailableAmount(), Arith.add(logParam.getAddAmount(), logParam.getOutAmount())));
        companyCapitalLogService.save(companyCapitalLog);

        boolean b = companyCapitalService.updateFrozenAmount(companyCapital, Arith.add(logParam.getAddAmount(), logParam.getOutAmount()));
        if (!b) {
            throw new ServiceException("修改资金账号失败", HttpStatus.CAPITAL_WARN);
        }
    }


    @Override
    @Transactional
    public void unFreeze(CompanyCapitalLogParam logParam) {
        Assert.notBlank(logParam.getOrderNo(), "业务单号不能为空");
        Assert.notNull(logParam.getCompanyId(), "企业ID不能为空");

        CompanyCapital companyCapital = companyCapitalService.queryOrInit(CompanyCapitalConsts.Types.DEPOSIT.getCode(), logParam.getCompanyId());

        CompanyCapitalLog companyCapitalLog = companyCapitalLogService.getOne(DynamicCondition.toWrapper(new CompanyCapitalLogQuery()
                .setCapitalId(companyCapital.getId()).setOrderNo(logParam.getOrderNo()).setType(logParam.getType()).setTradeId(logParam.getTradeId())));
        if (Objects.isNull(companyCapitalLog)) {
            return;
        }
        // 判断是否重复扣款了
        if (Arith.eq(companyCapitalLog.getOutAmount(), Objects.requireNonNullElse(logParam.getOutAmount(), BigDecimal.ZERO)) && Arith.eq(companyCapitalLog.getAddAmount(), Objects.requireNonNullElse(logParam.getAddAmount(), BigDecimal.ZERO))) {
            return;
        }
        BigDecimal addAmount = Arith.add(companyCapitalLog.getAddAmount(), logParam.getAddAmount());
        BigDecimal outAmount = Arith.add(companyCapitalLog.getOutAmount(), logParam.getOutAmount());
        if(Arith.gt(Arith.sub(addAmount, outAmount.abs()), BigDecimal.ZERO)){
            log.info("业务单号：{}，金额不一致，请检查, addAmount:{}, outAmount:{}", logParam.getOrderNo(), addAmount, outAmount);
            return;
        }
        //
        companyCapitalLog.setAddAmount(Arith.add(companyCapitalLog.getAddAmount(), logParam.getAddAmount())).setOutAmount(Arith.add(companyCapitalLog.getOutAmount(), logParam.getOutAmount()))
                .setAvailableAmount(Arith.add(companyCapital.getAvailableAmount(), companyCapitalLog.getAddAmount())).setUpdateTime(DateUtil.date())
                .setSubtotal(Arith.add(companyCapitalLog.getAddAmount(), companyCapitalLog.getOutAmount()));
        companyCapitalLogService.update(companyCapitalLog, new LambdaQueryWrapper<>(CompanyCapitalLog.class).eq(CompanyCapitalLog::getId, companyCapitalLog.getId()));
        // 修改资金账号
        boolean update = companyCapitalService.updateFrozenAmount(companyCapital, Arith.add(logParam.getAddAmount(), logParam.getOutAmount()));
        if (!update) {
            throw new ServiceException("修改资金账号失败", HttpStatus.CAPITAL_WARN);
        }
    }


    @Override
    public PageBO<CompanyCapitalLogBO> pageLog(CompanyCapitalLogQuery query, PageParamV2 pageParamV2) {

        PageUtils.startPage(pageParamV2);

        List<CompanyCapitalLog> list = companyCapitalLogService.list(DynamicCondition.toWrapper(query));


        return PageUtils.fromList(list, CompanyCapitalLogCov.INSTANCE::listToBO);


    }


    @Override
    public List<CompanyCapitalLogBO> listLog(CompanyCapitalLogQuery query) {
        List<CompanyCapitalLog> list = companyCapitalLogService.list(DynamicCondition.toWrapper(query));

        return CompanyCapitalLogCov.INSTANCE.listToBO(list);


    }


}
