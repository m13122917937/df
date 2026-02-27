package com.ruoyi.bill.facade;

import java.util.List;
import com.ruoyi.bill.domain.Payer;
import com.ruoyi.bill.model.bo.PayerBO;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.bill.model.param.PayerParam;
import com.ruoyi.bill.model.query.PayerQuery;

/**
 * 付款账号维护Service接口
 * 
 * @author ruoyi
 * @date 2025-11-07
 */
public interface IPayerFacade {

	List<PayerBO> list(PayerQuery query, SortBy sort);

	PageBO<PayerBO> listPage(PayerQuery query, PageParamV2 pageParam);

	PayerBO getOne(PayerQuery query);

    boolean save(PayerParam param);

	boolean update(PayerParam param,PayerQuery query);

}
