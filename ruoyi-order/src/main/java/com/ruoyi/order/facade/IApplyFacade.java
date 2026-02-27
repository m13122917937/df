package com.ruoyi.order.facade;

import java.util.List;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.order.domain.Apply;
import com.ruoyi.order.model.bo.ApplyBO;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.page.PageParam;
import com.ruoyi.order.model.param.ApplyParam;
import com.ruoyi.order.model.query.ApplyQuery;

/**
 * 【请填写功能名称】Service接口
 *
 * @author ruoyi
 * @date 2025-09-23
 */
public interface IApplyFacade {

    List<ApplyBO> list(ApplyQuery query);

    ApplyBO save(ApplyParam param);

    PageBO<ApplyBO> listPage(ApplyQuery query, PageParamV2 pageParamV2);

    ApplyBO getOne(ApplyQuery query);

    boolean update(ApplyParam param, ApplyQuery query);

    boolean remove(ApplyQuery query);
}
