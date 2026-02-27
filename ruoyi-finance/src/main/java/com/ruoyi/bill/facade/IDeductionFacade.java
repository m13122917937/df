package com.ruoyi.bill.facade;

import java.util.List;
import com.ruoyi.bill.domain.Deduction;
import com.ruoyi.bill.model.bo.DeductionBO;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.bill.model.param.DeductionParam;
import com.ruoyi.bill.model.query.DeductionQuery;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2025-12-10
 */
public interface IDeductionFacade {

	List<DeductionBO> list(DeductionQuery query, SortBy sort);

	PageBO<DeductionBO> listPage(DeductionQuery query, PageParamV2 pageParam);

	DeductionBO save(DeductionParam param);

	DeductionBO getOne(DeductionQuery query);

	boolean update(DeductionParam param,DeductionQuery query);

}
