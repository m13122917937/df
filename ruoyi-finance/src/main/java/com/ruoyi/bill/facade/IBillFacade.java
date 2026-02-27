package com.ruoyi.bill.facade;

import java.util.List;

import com.ruoyi.bill.domain.Bill;
import com.ruoyi.bill.model.bo.BillBO;
import com.ruoyi.bill.model.bo.BillSUMBO;
import com.ruoyi.bill.model.bo.PaymentOperationsBO;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.bill.model.param.BillParam;
import com.ruoyi.bill.model.query.BillQuery;

/**
 * 账单Service接口
 *
 * @author ruoyi
 * @date 2025-11-07
 */
public interface IBillFacade {

    List<BillBO> list(BillQuery query, SortBy sort);

    PageBO<BillBO> listPage(BillQuery query, PageParamV2 pageParam);

    BillBO getOne(BillQuery query);

    boolean update(BillParam param, BillQuery query);

    BillBO save(BillParam param);

    PageBO<PaymentOperationsBO> pageSum(BillQuery billQuery, PageParamV2 pageParamV2);

    BillSUMBO sum(BillQuery billQuery);
}
