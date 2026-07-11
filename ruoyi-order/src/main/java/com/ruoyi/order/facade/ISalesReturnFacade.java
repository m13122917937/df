package com.ruoyi.order.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.order.model.bo.SalesReturnBO;
import com.ruoyi.order.model.param.SalesReturnParam;
import com.ruoyi.order.model.query.SalesReturnQuery;

/**
 * 销售退货单Service接口
 *
 * @author ruoyi
 * @date 2026-06-30
 */
public interface ISalesReturnFacade {

    PageBO<SalesReturnBO> listPage(SalesReturnQuery query, PageParamV2 pageParam);

    SalesReturnBO getOne(SalesReturnQuery query);

    SalesReturnBO save(SalesReturnParam param);

    boolean update(SalesReturnParam param, SalesReturnQuery query);
}
