package com.ruoyi.express.facade;

import java.util.List;
import com.ruoyi.express.domain.ExpressContrast;
import com.ruoyi.express.model.bo.ExpressContrastBO;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.express.model.param.ExpressContrastParam;
import com.ruoyi.express.model.query.ExpressContrastQuery;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2025-10-11
 */
public interface IExpressContrastFacade {

	List<ExpressContrastBO> list(ExpressContrastQuery query, SortBy sort);

	PageBO<ExpressContrastBO> listPage(ExpressContrastQuery query, PageParamV2 pageParam);

	ExpressContrastBO getOne(ExpressContrastQuery query);

	boolean update(ExpressContrastParam param,ExpressContrastQuery query);

}
