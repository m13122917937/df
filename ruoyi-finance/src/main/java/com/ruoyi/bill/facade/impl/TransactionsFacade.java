package com.ruoyi.bill.facade.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
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

import java.util.List;


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

}
