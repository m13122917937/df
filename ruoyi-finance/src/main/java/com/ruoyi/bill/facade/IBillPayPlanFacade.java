package com.ruoyi.bill.facade;

import java.util.List;
import com.ruoyi.bill.domain.BillPayPlan;
import com.ruoyi.bill.model.bo.BillPayPlanBO;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.bill.model.param.BillPayPlanParam;
import com.ruoyi.bill.model.query.BillPayPlanQuery;

/**
 * 排款计划Service接口
 * 
 * @author ruoyi
 * @date 2025-11-09
 */
public interface IBillPayPlanFacade {

	List<BillPayPlanBO> list(BillPayPlanQuery query, SortBy sort);

	PageBO<BillPayPlanBO> listPage(BillPayPlanQuery query, PageParamV2 pageParam);

	BillPayPlanBO getOne(BillPayPlanQuery query);

	boolean delete(BillPayPlanQuery query);

	BillPayPlanBO save(BillPayPlanParam param);

	boolean update(BillPayPlanParam param,BillPayPlanQuery query);

}
