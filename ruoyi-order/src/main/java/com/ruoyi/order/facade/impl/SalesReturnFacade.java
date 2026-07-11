package com.ruoyi.order.facade.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.order.convert.OrderCov;
import com.ruoyi.order.convert.SalesReturnCov;
import com.ruoyi.order.domain.Order;
import com.ruoyi.order.domain.SalesReturn;
import com.ruoyi.order.facade.ISalesReturnFacade;
import com.ruoyi.order.model.bo.SalesReturnBO;
import com.ruoyi.order.model.param.SalesReturnParam;
import com.ruoyi.order.model.query.SalesReturnQuery;
import com.ruoyi.order.service.SalesReturnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 销售退货单Service业务层处理
 *
 * @author ruoyi
 * @date 2026-06-30
 */
@Slf4j
@Service
public class SalesReturnFacade implements ISalesReturnFacade {

    @Autowired
    private SalesReturnService salesReturnService;

    @Override
    public PageBO<SalesReturnBO> listPage(SalesReturnQuery query, PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<SalesReturn> list = salesReturnService.list(DynamicCondition.toWrapper(query, pageParam.getSort()));
        return PageUtils.fromList(list, SalesReturnCov.INSTANCE::listToBO);

    }

    @Override
    public SalesReturnBO getOne(SalesReturnQuery query) {
        Wrapper<SalesReturn> wrapper = DynamicCondition.toWrapper(query);
        SalesReturn domain = salesReturnService.getOne(wrapper);
        if (domain == null) {
            return null;
        }
        return SalesReturnCov.INSTANCE.toBO(domain);
    }

    @Override
    public SalesReturnBO save(SalesReturnParam param) {
        SalesReturn domain = SalesReturnCov.INSTANCE.paramToDomain(param);
        salesReturnService.save(domain);
        return SalesReturnCov.INSTANCE.toBO(domain);
    }

    @Override
    public boolean update(SalesReturnParam param, SalesReturnQuery query) {
        return salesReturnService.update(SalesReturnCov.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }
}
