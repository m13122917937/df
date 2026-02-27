package com.ruoyi.order.facade.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.order.convert.PddOrderIncrementCov;
import com.ruoyi.order.domain.PddOrderIncrement;
import com.ruoyi.order.facade.IPddOrderIncrementFacade;
import com.ruoyi.order.manager.PddOrderIncrementManager;
import com.ruoyi.order.model.bo.PddOrderIncrementBO;
import com.ruoyi.order.model.param.PddOrderIncrementParam;
import com.ruoyi.order.model.query.PddOrderIncrementQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 拼多多增量订单Service接口实现
 *
 * @author ruoyi
 * @date 2025-02-08
 */
@Service
public class PddOrderIncrementFacadeService implements IPddOrderIncrementFacade {

    @Autowired
    private PddOrderIncrementManager pddOrderIncrementManager;

    @Override
    public PddOrderIncrementBO save(PddOrderIncrementParam param) {
        PddOrderIncrement domain = PddOrderIncrementCov.INSTANCE.paramToDomain(param);
        domain.setCreateTime(DateUtil.date());
        pddOrderIncrementManager.save(domain);
        return PddOrderIncrementCov.INSTANCE.toBO(domain);
    }

    @Override
    public boolean update(PddOrderIncrementParam param, PddOrderIncrementQuery query) {
        PddOrderIncrement queryDomain = PddOrderIncrementCov.INSTANCE.queryToDomain(query);
        PddOrderIncrement order = PddOrderIncrementCov.INSTANCE.paramToDomain(param);
        return pddOrderIncrementManager.update(order, new QueryWrapper<>(queryDomain));
    }

    @Override
    public PddOrderIncrementBO getByOrderSn(String orderSn) {
        QueryWrapper<PddOrderIncrement> wrapper = new QueryWrapper<>();
        wrapper.eq("order_sn", orderSn);
        PddOrderIncrement domain = pddOrderIncrementManager.getOne(wrapper);
        return PddOrderIncrementCov.INSTANCE.toBO(domain);
    }

    @Override
    public List<PddOrderIncrementBO> list(PddOrderIncrementQuery query) {
        PddOrderIncrement domain = PddOrderIncrementCov.INSTANCE.queryToDomain(query);
        List<PddOrderIncrement> list = pddOrderIncrementManager.list(new QueryWrapper<>(domain));
        return PddOrderIncrementCov.INSTANCE.listToBO(list);
    }

    @Override
    public PddOrderIncrementBO saveOrUpdate(PddOrderIncrementParam param) {
        // 先查询订单是否存在
        PddOrderIncrementBO existingOrder = getByOrderSn(param.getOrderSn());

        if (existingOrder != null) {
            // 存在则更新
            PddOrderIncrementQuery query = new PddOrderIncrementQuery().setOrderSn(param.getOrderSn());
            update(param, query);
            return getByOrderSn(param.getOrderSn());
        } else {
            // 不存在则保存
            return save(param);
        }
    }
}