package com.ruoyi.order.facade.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.order.convert.ApplyCov;
import com.ruoyi.order.domain.Apply;
import com.ruoyi.order.facade.IApplyFacade;
import com.ruoyi.order.service.ApplyService;
import com.ruoyi.order.model.bo.ApplyBO;
import com.ruoyi.order.model.param.ApplyParam;
import com.ruoyi.order.model.query.ApplyQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 【请填写功能名称】Service接口
 *
 * @author ruoyi
 * @date 2025-09-23
 */
@Slf4j
@Service
public class ApplyFacade implements IApplyFacade {

    @Autowired
    private ApplyService applyService;

    @Override
    public List<ApplyBO> list(ApplyQuery query) {

        return ApplyCov.INSTANCE.listToBO(applyService.list(DynamicCondition.toWrapper(query)));
    }

    @Override
    public ApplyBO save(ApplyParam param) {
        Apply apply = ApplyCov.INSTANCE.paramToDomain(param);
        apply.setCreateTime(DateUtil.date());
        apply.setUpdateTime(DateUtil.date());
        applyService.save(apply);
        return ApplyCov.INSTANCE.toBO(apply);
    }


    @Override
    public PageBO<ApplyBO> listPage(final ApplyQuery query, final PageParamV2 pageParam) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getSize());
        Wrapper<Apply> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
        return PageUtils.fromList(applyService.list(wrapper), ApplyCov.INSTANCE::listToBO);
    }


    @Override
    public ApplyBO getOne(ApplyQuery query) {
        return ApplyCov.INSTANCE.toBO(applyService.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public boolean update(ApplyParam param, ApplyQuery query) {
        return applyService.update(ApplyCov.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }

    @Override
    public boolean remove(ApplyQuery query) {
        return applyService.remove(DynamicCondition.toWrapper(query));
    }

}
