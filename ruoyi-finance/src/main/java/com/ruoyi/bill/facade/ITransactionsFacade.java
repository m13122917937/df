package com.ruoyi.bill.facade;

import java.util.List;
import com.ruoyi.bill.domain.Transactions;
import com.ruoyi.bill.model.bo.TransactionsBO;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.bill.model.param.TransactionsParam;
import com.ruoyi.bill.model.query.TransactionsQuery;

/**
 * 财务流水明细Service接口
 * 
 * @author ruoyi
 * @date 2025-12-01
 */
public interface ITransactionsFacade {

	List<TransactionsBO> list(TransactionsQuery query, SortBy sort);

	PageBO<TransactionsBO> listPage(TransactionsQuery query, PageParamV2 pageParam);

	TransactionsBO save(TransactionsParam param);

	TransactionsBO getOne(TransactionsQuery query);

	boolean update(TransactionsParam param,TransactionsQuery query);

    void delete(TransactionsQuery transactionsQuery);


}
