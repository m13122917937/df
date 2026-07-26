package com.ruoyi.master.facade.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.master.convert.MasterSubjectBankTransactionConvert;
import com.ruoyi.master.domain.MasterSubjectBankTransaction;
import com.ruoyi.master.facade.IMasterSubjectBankTransactionFacade;
import com.ruoyi.master.service.MasterSubjectBankTransactionService;
import com.ruoyi.master.model.bo.MasterSubjectBankTransactionBO;
import com.ruoyi.master.model.param.MasterSubjectBankTransactionParam;
import com.ruoyi.master.model.query.MasterSubjectBankTransactionQuery;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 主体银行卡资金流水明细对外接口实现（原 TransactionsFacade）。
 *
 * @author ruoyi
 * @date 2025-12-01
 */
@Slf4j
@Service
public class MasterSubjectBankTransactionFacade implements IMasterSubjectBankTransactionFacade {

    @Autowired
    private MasterSubjectBankTransactionService masterSubjectBankTransactionService;

    @Override
    public List<MasterSubjectBankTransactionBO> list(MasterSubjectBankTransactionQuery query, SortBy sort) {
        return MasterSubjectBankTransactionConvert.INSTANCE.listToBO(
                masterSubjectBankTransactionService.list(DynamicCondition.toWrapper(query, sort)));
    }


    @Override
    public PageBO<MasterSubjectBankTransactionBO> listPage(final MasterSubjectBankTransactionQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        Wrapper<MasterSubjectBankTransaction> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
        return PageUtils.fromList(masterSubjectBankTransactionService.list(wrapper), MasterSubjectBankTransactionConvert.INSTANCE::listToBO);
    }

    @Override
    public MasterSubjectBankTransactionBO save(final MasterSubjectBankTransactionParam param) {
        MasterSubjectBankTransaction transactions = MasterSubjectBankTransactionConvert.INSTANCE.toEntity(param);
        masterSubjectBankTransactionService.save(transactions);
        return MasterSubjectBankTransactionConvert.INSTANCE.toBO(transactions);
    }


    @Override
    public MasterSubjectBankTransactionBO getOne(MasterSubjectBankTransactionQuery query) {
        return MasterSubjectBankTransactionConvert.INSTANCE.toBO(masterSubjectBankTransactionService.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public boolean update(MasterSubjectBankTransactionParam param, MasterSubjectBankTransactionQuery query) {
        return masterSubjectBankTransactionService.update(MasterSubjectBankTransactionConvert.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }

    @Override
    public void delete(MasterSubjectBankTransactionQuery transactionsQuery) {
        masterSubjectBankTransactionService.remove(DynamicCondition.toWrapper(transactionsQuery));
    }

    @Override
    public void updateCounterpartyTotal(Long accountId, String counterparty) {
        if (accountId == null || StrUtil.isBlank(counterparty)) {
            return;
        }
        // 计算该交易方的净额
        QueryWrapper<MasterSubjectBankTransaction> query = Wrappers.query();
        query.select("COALESCE(SUM(CASE WHEN category = 0 THEN amount ELSE -amount END), 0) as total")
                .eq("account_id", accountId)
                .eq("counterparty", counterparty)
                .eq("deleted", 0);
        List<Map<String, Object>> maps = masterSubjectBankTransactionService.listMaps(query);
        BigDecimal total = BigDecimal.ZERO;
        if (CollUtil.isNotEmpty(maps) && maps.get(0) != null) {
            Object totalObj = maps.get(0).get("total");
            if (totalObj instanceof BigDecimal) {
                total = (BigDecimal) totalObj;
            } else if (totalObj instanceof Number) {
                total = BigDecimal.valueOf(((Number) totalObj).doubleValue());
            }
        }
        // 更新该交易方的所有记录
        MasterSubjectBankTransaction update = new MasterSubjectBankTransaction();
        update.setTotalAmountByCounterparty(total);
        masterSubjectBankTransactionService.update(update, Wrappers.<MasterSubjectBankTransaction>lambdaUpdate()
                .eq(MasterSubjectBankTransaction::getAccountId, accountId)
                .eq(MasterSubjectBankTransaction::getCounterparty, counterparty));
    }

}
