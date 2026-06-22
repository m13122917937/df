package com.ruoyi.bill.facade.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.bill.convert.ContractCov;
import com.ruoyi.bill.domain.Contract;
import com.ruoyi.bill.facade.IContractFacade;
import com.ruoyi.bill.model.bo.ContractBO;
import com.ruoyi.bill.model.param.ContractParam;
import com.ruoyi.bill.model.query.ContractQuery;
import com.ruoyi.bill.service.ContractService;
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
 * 合同 Facade 实现
 *
 * @author ruoyi
 * @date 2026-06-15
 */
@Slf4j
@Service
public class ContractFacade implements IContractFacade {

    @Autowired
    private ContractService contractService;

    @Override
    public List<ContractBO> list(ContractQuery query, SortBy sort) {
        return ContractCov.INSTANCE.listToBO(contractService.list(DynamicCondition.toWrapper(query, sort)));
    }

    @Override
    public PageBO<ContractBO> listPage(ContractQuery query, PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        Wrapper<Contract> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
        return PageUtils.fromList(contractService.list(wrapper), ContractCov.INSTANCE::listToBO);
    }

    @Override
    public ContractBO getOne(ContractQuery query) {
        return ContractCov.INSTANCE.toBO(contractService.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public Long save(ContractParam param) {
        Contract entity = ContractCov.INSTANCE.paramToDomain(param);
        contractService.save(entity);
        return entity.getId();
    }

    @Override
    public boolean update(ContractParam param, ContractQuery query) {
        return contractService.update(ContractCov.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }

    @Override
    public boolean removeById(Long id) {
        return contractService.removeById(id);
    }
}
