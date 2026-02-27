package com.ruoyi.order.facade.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.order.convert.HangingOrderCov;
import com.ruoyi.order.domain.HangingOrder;
import com.ruoyi.order.facade.IHangingOrderFacade;
import com.ruoyi.order.manager.HangingOrderManager;
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
public class HangingOrderFacadeService implements IHangingOrderFacade {

    @Autowired
    private HangingOrderManager hangingOrderManager;

    @Override
    public List<HangingOrderBO> list(HangingOrderQuery query) {
        Wrapper<HangingOrder> wrapper = DynamicCondition.toWrapper(query);
        return HangingOrderCov.INSTANCE.listToBO(hangingOrderManager.list(wrapper));
    }

    @Override
    public HangingOrderBO getOne(HangingOrderQuery query) {
        Wrapper<HangingOrder> wrapper = DynamicCondition.toWrapper(query);
        return HangingOrderCov.INSTANCE.toBO(hangingOrderManager.getOne(wrapper));
    }

    @Override
    public long count(HangingOrderQuery query) {
        Wrapper<HangingOrder> wrapper = DynamicCondition.toWrapper(query);
        return hangingOrderManager.count(wrapper);
    }

    @Override
    public boolean update(HangingOrderParam param, HangingOrderQuery query) {
        param.setCreateTime(DateUtil.date());
        Wrapper<HangingOrder> wrapper = DynamicCondition.toWrapper(query);
        return hangingOrderManager.update(HangingOrderCov.INSTANCE.paramToDomain(param), wrapper);
    }

    @Override
    public HangingOrderBO save(final HangingOrderParam hangingOrderParam) {
        HangingOrder hangingOrder = HangingOrderCov.INSTANCE.paramToDomain(hangingOrderParam);
        hangingOrder.setCreateTime(DateUtil.date());
        hangingOrderManager.save(hangingOrder);
        return HangingOrderCov.INSTANCE.toBO(hangingOrder);
    }

}
