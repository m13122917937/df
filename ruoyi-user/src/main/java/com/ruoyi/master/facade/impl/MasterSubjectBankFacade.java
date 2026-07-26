package com.ruoyi.master.facade.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.master.domain.MasterSubjectBank;
import com.ruoyi.master.facade.IMasterSubjectBankFacade;
import com.ruoyi.master.model.query.MasterSubjectBankQuery;
import com.ruoyi.master.model.bo.MasterSubjectBankBO;
import com.ruoyi.master.model.param.MasterSubjectBankParam;
import com.ruoyi.master.convert.MasterSubjectBankConvert;
import com.ruoyi.master.service.MasterSubjectBankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 主体银行卡维护对外接口实现（原 PayerFacade）。
 *
 * @author ruoyi
 * @date 2025-11-07
 */
@Slf4j
@Service
public class MasterSubjectBankFacade implements IMasterSubjectBankFacade {

    @Autowired
    private MasterSubjectBankService masterSubjectBankService;

    @Override
    public List<MasterSubjectBankBO> list(MasterSubjectBankQuery query, SortBy sort) {
        return MasterSubjectBankConvert.INSTANCE.listToBO(
                masterSubjectBankService.list(DynamicCondition.toWrapper(query, sort)));
    }


    @Override
    public PageBO<MasterSubjectBankBO> listPage(final MasterSubjectBankQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        Wrapper<MasterSubjectBank> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
        return PageUtils.fromList(masterSubjectBankService.list(wrapper), MasterSubjectBankConvert.INSTANCE::listToBO);
    }


    @Override
    public MasterSubjectBankBO getOne(MasterSubjectBankQuery query) {
        return MasterSubjectBankConvert.INSTANCE.toBO(masterSubjectBankService.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public boolean save(MasterSubjectBankParam param) {
        return masterSubjectBankService.save(MasterSubjectBankConvert.INSTANCE.paramToDomain(param));
    }

    @Override
    public boolean update(MasterSubjectBankParam param, MasterSubjectBankQuery query) {
        return masterSubjectBankService.update(MasterSubjectBankConvert.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }

}
