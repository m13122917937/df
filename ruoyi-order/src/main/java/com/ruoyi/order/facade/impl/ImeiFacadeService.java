package com.ruoyi.order.facade.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.order.convert.ImeiCov;
import com.ruoyi.order.domain.Imei;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.manager.ImeiManager;
import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.order.model.query.ImeiQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 订单串码Service接口
 *
 * @author ruoyi
 * @date 2025-09-11
 */
@Slf4j
@Service
public class ImeiFacadeService implements IImeiFacade {

    @Autowired
    private ImeiManager imeiManager;

    @Override
    public List<ImeiBO> list(ImeiQuery query) {
        Wrapper<Imei> wrapper = DynamicCondition.toWrapper(query);

        return ImeiCov.INSTANCE.listToBO(imeiManager.list(wrapper));
    }

    @Override
    public ImeiBO save(ImeiParam param) {
        Imei domain = ImeiCov.INSTANCE.paramToDomain(param);
        imeiManager.save(domain);
        return ImeiCov.INSTANCE.toBO(domain);
    }

    @Override
    public ImeiBO getOne(ImeiQuery query) {
        Imei domain = ImeiCov.INSTANCE.queryToDomain(query);
        return ImeiCov.INSTANCE.toBO(imeiManager.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public boolean update(ImeiParam param, ImeiQuery query) {
        Imei queryDomain = ImeiCov.INSTANCE.queryToDomain(query);
        return imeiManager.update(ImeiCov.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }

    @Override
    public boolean delete(ImeiQuery imeiQuery) {
        return imeiManager.remove(DynamicCondition.toWrapper(imeiQuery));
    }

    @Override
    public long count(ImeiQuery imeiQuery) {
        return imeiManager.count(DynamicCondition.toWrapper(imeiQuery));
    }

}
