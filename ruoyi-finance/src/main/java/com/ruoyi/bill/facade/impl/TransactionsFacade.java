package com.ruoyi.bill.facade.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.bill.convert.TransactionsCov;
import com.ruoyi.bill.domain.Transactions;
import com.ruoyi.bill.facade.ITransactionsFacade;
import com.ruoyi.bill.service.TransactionsService;
import com.ruoyi.bill.model.bo.TransactionsBO;
import com.ruoyi.bill.model.param.TransactionsParam;
import com.ruoyi.bill.model.query.TransactionsQuery;
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
 * 财务流水明细Service接口
 *
 * @author ruoyi
 * @date 2025-12-01
 */
@Slf4j
@Service
public class TransactionsFacade implements ITransactionsFacade {

    @Autowired
    private TransactionsService transactionsService;

    @Override
    public List<TransactionsBO> list(TransactionsQuery query, SortBy sort) {

        return TransactionsCov.INSTANCE.listToBO(transactionsService.list(DynamicCondition.toWrapper(query, sort)));
    }


    @Override
    public PageBO<TransactionsBO> listPage(final TransactionsQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        Wrapper<Transactions> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
        return PageUtils.fromList(transactionsService.list(wrapper), TransactionsCov.INSTANCE::listToBO);
    }

    @Override
    public TransactionsBO save(final TransactionsParam param) {
        Transactions transactions = TransactionsCov.INSTANCE.toEntity(param);
        transactionsService.save(transactions);
        return TransactionsCov.INSTANCE.toBO(transactions);
    }


    @Override
    public TransactionsBO getOne(TransactionsQuery query) {
        return TransactionsCov.INSTANCE.toBO(transactionsService.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public boolean update(TransactionsParam param, TransactionsQuery query) {
        return transactionsService.update(TransactionsCov.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }

    @Override
    public void delete(TransactionsQuery transactionsQuery) {
        transactionsService.remove(DynamicCondition.toWrapper(transactionsQuery));
    }

    @Override
    public void updateCounterpartyTotal(Long accountId, String counterparty) {
        if (accountId == null || StrUtil.isBlank(counterparty)) {
            return;
        }
        // 计算该交易方的净额
        QueryWrapper<Transactions> query = Wrappers.query();
        query.select("COALESCE(SUM(CASE WHEN category = 0 THEN amount ELSE -amount END), 0) as total")
                .eq("account_id", accountId)
                .eq("counterparty", counterparty)
                .eq("deleted", 0);
        List<Map<String, Object>> maps = transactionsService.listMaps(query);
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
        Transactions update = new Transactions();
        update.setTotalAmountByCounterparty(total);
        transactionsService.update(update, Wrappers.<Transactions>lambdaUpdate()
                .eq(Transactions::getAccountId, accountId)
                .eq(Transactions::getCounterparty, counterparty));
    }

}
