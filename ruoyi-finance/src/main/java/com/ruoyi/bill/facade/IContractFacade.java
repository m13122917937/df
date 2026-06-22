package com.ruoyi.bill.facade;

import com.ruoyi.bill.model.bo.ContractBO;
import com.ruoyi.bill.model.param.ContractParam;
import com.ruoyi.bill.model.query.ContractQuery;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;

import java.util.List;

/**
 * 合同 Facade
 *
 * @author ruoyi
 * @date 2026-06-15
 */
public interface IContractFacade {

    List<ContractBO> list(ContractQuery query, SortBy sort);

    PageBO<ContractBO> listPage(ContractQuery query, PageParamV2 pageParam);

    ContractBO getOne(ContractQuery query);

    Long save(ContractParam param);

    boolean update(ContractParam param, ContractQuery query);

    boolean removeById(Long id);
}
