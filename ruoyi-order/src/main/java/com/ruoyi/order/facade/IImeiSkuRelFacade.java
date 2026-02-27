package com.ruoyi.order.facade;

import java.util.List;
import com.ruoyi.order.domain.ImeiSkuRel;
import com.ruoyi.order.model.bo.ImeiSkuRelBO;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.order.model.param.ImeiSkuRelParam;
import com.ruoyi.order.model.query.ImeiSkuRelQuery;

/**
 * 型号映射决策Service接口
 * 
 * @author ruoyi
 * @date 2025-10-05
 */
public interface IImeiSkuRelFacade {

	List<ImeiSkuRelBO> list(ImeiSkuRelQuery query, SortBy sort);

	PageBO<ImeiSkuRelBO> listPage(ImeiSkuRelQuery query, PageParamV2 pageParam);

	ImeiSkuRelBO getOne(ImeiSkuRelQuery query);

	boolean update(ImeiSkuRelParam param,ImeiSkuRelQuery query);

    ImeiSkuRelBO save(ImeiSkuRelParam imeiSkuRelParam);

    Boolean del(ImeiSkuRelQuery query);

    Long count(ImeiSkuRelQuery imeiSkuRelQuery);

}
