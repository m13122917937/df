package com.ruoyi.bill.facade;

import java.util.List;
import com.ruoyi.bill.domain.PayerConfig;
import com.ruoyi.bill.model.bo.PayerConfigBO;
import com.ruoyi.bill.model.param.PayerParam;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.bill.model.param.PayerConfigParam;
import com.ruoyi.bill.model.query.PayerConfigQuery;

/**
 * 付款配置Service接口
 * 
 * @author ruoyi
 * @date 2025-11-07
 */
public interface IPayerConfigFacade {

	List<PayerConfigBO> list(PayerConfigQuery query, SortBy sort);

	PageBO<PayerConfigBO> listPage(PayerConfigQuery query, PageParamV2 pageParam);

	PayerConfigBO getOne(PayerConfigQuery query);

	boolean update(PayerConfigParam param,PayerConfigQuery query);

    void save(PayerConfigParam param);

    long count(PayerConfigQuery payerConfigQuery);

}
