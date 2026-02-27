package com.ruoyi.bill.facade.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.bill.convert.BillCov;
import com.ruoyi.bill.domain.Bill;
import com.ruoyi.bill.domain.dto.BillSUMDTO;
import com.ruoyi.bill.domain.dto.PaymentOperationsDTO;
import com.ruoyi.bill.facade.IBillFacade;
import com.ruoyi.bill.manager.BillManager;
import com.ruoyi.bill.model.bo.BillBO;
import com.ruoyi.bill.model.bo.BillSUMBO;
import com.ruoyi.bill.model.bo.PaymentOperationsBO;
import com.ruoyi.bill.model.param.BillParam;
import com.ruoyi.bill.model.query.BillQuery;
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
 * 账单Service接口
 *
 * @author ruoyi
 * @date 2025-11-07
 */
@Slf4j
@Service
public class BillFacadeService implements IBillFacade {

    @Autowired
    private BillManager billManager;

    @Override
    public List<BillBO> list(BillQuery query, SortBy sort) {

        return BillCov.INSTANCE.listToBO(billManager.list(DynamicCondition.toWrapper(query)));
    }


    @Override
    public PageBO<BillBO> listPage(final BillQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        Wrapper<Bill> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
        return PageUtils.fromList(billManager.list(wrapper), BillCov.INSTANCE::listToBO);
    }


    @Override
    public BillBO getOne(BillQuery query) {
        return BillCov.INSTANCE.toBO(billManager.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public boolean update(BillParam param, BillQuery query) {
        return billManager.update(BillCov.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }

    @Override
    public BillBO save(BillParam param) {
        Bill bill = BillCov.INSTANCE.paramToDomain(param);
        billManager.save(bill);
        return BillCov.INSTANCE.toBO(bill);
    }

    @Override
    public PageBO<PaymentOperationsBO> pageSum(BillQuery billQuery, PageParamV2 pageParamV2) {
        PageUtils.startPage(pageParamV2);
        List<PaymentOperationsDTO> paymentOperationsDTOS = billManager.getBaseMapper().paymentOperations(billQuery);
        return PageUtils.fromList(paymentOperationsDTOS, BillCov.INSTANCE::dtoTOBO);
    }


    @Override
    public BillSUMBO sum(BillQuery billQuery) {
        BillSUMDTO billSUMDTOS = billManager.getBaseMapper().selectBillSum(billQuery);

        return BillCov.INSTANCE.toSumBO(billSUMDTOS);
    }

}
