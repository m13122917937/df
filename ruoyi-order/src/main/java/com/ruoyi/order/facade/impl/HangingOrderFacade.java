package com.ruoyi.order.facade.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.order.convert.HangingOrderCov;
import com.ruoyi.order.domain.HangingOrder;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.service.HangingOrderService;
import com.ruoyi.order.model.bo.HangingOrderBO;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.query.HangingOrderQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 挂单Service接口
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Service
public class HangingOrderFacade implements IHangingOrderFacade {

    @Autowired
    private HangingOrderService hangingOrderService;

    @Override
    public List<HangingOrderBO> list(HangingOrderQuery query) {
        Wrapper<HangingOrder> wrapper = DynamicCondition.toWrapper(query);
        return HangingOrderCov.INSTANCE.listToBO(hangingOrderService.list(wrapper));
    }

    @Override
    public HangingOrderBO getOne(HangingOrderQuery query) {
        Wrapper<HangingOrder> wrapper = DynamicCondition.toWrapper(query);
        return HangingOrderCov.INSTANCE.toBO(hangingOrderService.getOne(wrapper));
    }

    @Override
    public long count(HangingOrderQuery query) {
        Wrapper<HangingOrder> wrapper = DynamicCondition.toWrapper(query);
        return hangingOrderService.count(wrapper);
    }

    @Override
    public boolean update(HangingOrderParam param, HangingOrderQuery query) {
        param.setCreateTime(DateUtil.date());
        Wrapper<HangingOrder> wrapper = DynamicCondition.toWrapper(query);
        return hangingOrderService.update(HangingOrderCov.INSTANCE.paramToDomain(param), wrapper);
    }

    @Override
    public HangingOrderBO save(final HangingOrderParam hangingOrderParam) {
        HangingOrder hangingOrder = HangingOrderCov.INSTANCE.paramToDomain(hangingOrderParam);
        hangingOrder.setCreateTime(DateUtil.date());
        hangingOrderService.save(hangingOrder);
        return HangingOrderCov.INSTANCE.toBO(hangingOrder);
    }

}
