package com.ruoyi.order.facade.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.order.convert.ImeiSkuRelCov;
import com.ruoyi.order.domain.ImeiSkuRel;
import com.ruoyi.order.facade.IImeiSkuRelFacade;
import com.ruoyi.order.service.ImeiSkuRelService;
import com.ruoyi.order.model.bo.ImeiSkuRelBO;
import com.ruoyi.order.model.param.ImeiSkuRelParam;
import com.ruoyi.order.model.query.ImeiSkuRelQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 型号映射决策Service接口
 *
 * @author ruoyi
 * @date 2025-10-05
 */
@Slf4j
@Service
public class ImeiSkuRelFacade implements IImeiSkuRelFacade {

    @Autowired
    private ImeiSkuRelService imeiSkuRelService;

    @Override
    public List<ImeiSkuRelBO> list(ImeiSkuRelQuery query, SortBy sort) {

        return ImeiSkuRelCov.INSTANCE.listToBO(imeiSkuRelService.list(DynamicCondition.toWrapper(query)));
    }


    @Override
    public PageBO<ImeiSkuRelBO> listPage(final ImeiSkuRelQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        Wrapper<ImeiSkuRel> wrapper = DynamicCondition.toWrapper(query, pageParam.getSort());
        return PageUtils.fromList(imeiSkuRelService.list(wrapper), ImeiSkuRelCov.INSTANCE::listToBO);
    }


    @Override
    public ImeiSkuRelBO getOne(ImeiSkuRelQuery query) {
        return ImeiSkuRelCov.INSTANCE.toBO(imeiSkuRelService.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public boolean update(ImeiSkuRelParam param, ImeiSkuRelQuery query) {
        return imeiSkuRelService.update(ImeiSkuRelCov.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }

    @Override
    public ImeiSkuRelBO save(ImeiSkuRelParam imeiSkuRelParam) {
        ImeiSkuRel imeiSkuRel = ImeiSkuRelCov.INSTANCE.paramToDomain(imeiSkuRelParam);

        boolean save = imeiSkuRelService.save(imeiSkuRel);

        return ImeiSkuRelCov.INSTANCE.toBO(imeiSkuRel);
    }

    @Override
    public Boolean del(ImeiSkuRelQuery query) {

        return imeiSkuRelService.remove(DynamicCondition.toWrapper(query));
    }

    @Override
    public Long count(ImeiSkuRelQuery imeiSkuRelQuery) {
        return imeiSkuRelService.count(DynamicCondition.toWrapper(imeiSkuRelQuery));
    }

}
