package com.ruoyi.master.facade;

import com.ruoyi.master.model.bo.MasterSubjectBankTransactionBO;
import com.ruoyi.master.model.param.MasterSubjectBankTransactionParam;
import com.ruoyi.master.model.query.MasterSubjectBankTransactionQuery;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;

import java.util.List;

/**
 * 主体银行卡资金流水明细对外接口（原 ITransactionsFacade）。
 *
 * @author ruoyi
 * @date 2025-12-01
 */
public interface IMasterSubjectBankTransactionFacade {

    List<MasterSubjectBankTransactionBO> list(MasterSubjectBankTransactionQuery query, SortBy sort);

    PageBO<MasterSubjectBankTransactionBO> listPage(MasterSubjectBankTransactionQuery query, PageParamV2 pageParam);

    MasterSubjectBankTransactionBO save(MasterSubjectBankTransactionParam param);

    MasterSubjectBankTransactionBO getOne(MasterSubjectBankTransactionQuery query);

    boolean update(MasterSubjectBankTransactionParam param, MasterSubjectBankTransactionQuery query);

    void delete(MasterSubjectBankTransactionQuery transactionsQuery);

    /**
     * 重新计算并更新指定 accountId + counterparty 的交易总额。
     *
     * @param accountId 账户ID
     * @param counterparty 交易对方
     */
    void updateCounterpartyTotal(Long accountId, String counterparty);

}
