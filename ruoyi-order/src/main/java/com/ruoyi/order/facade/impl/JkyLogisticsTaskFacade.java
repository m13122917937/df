package com.ruoyi.order.facade.impl;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.order.convert.JkyLogisticsTaskCov;
import com.ruoyi.order.domain.JkyLogisticsTask;
import com.ruoyi.order.facade.IJkyLogisticsTaskFacade;
import com.ruoyi.order.model.bo.JkyLogisticsTaskBO;
import com.ruoyi.order.model.param.JkyLogisticsTaskParam;
import com.ruoyi.order.model.query.JkyLogisticsTaskQuery;
import com.ruoyi.order.service.JkyLogisticsTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 吉客云物流更新延迟任务Facade实现
 *
 * @author ruoyi
 * @date 2026-06-24
 */
@Service
public class JkyLogisticsTaskFacade implements IJkyLogisticsTaskFacade {

    @Autowired
    private JkyLogisticsTaskService jkyLogisticsTaskService;

    @Override
    public List<JkyLogisticsTaskBO> list(JkyLogisticsTaskQuery query) {
        return JkyLogisticsTaskCov.INSTANCE.listToBO(
                jkyLogisticsTaskService.list(DynamicCondition.toWrapper(query)));
    }

    @Override
    public JkyLogisticsTaskBO save(JkyLogisticsTaskParam param) {
        JkyLogisticsTask domain = JkyLogisticsTaskCov.INSTANCE.paramToDomain(param);
        jkyLogisticsTaskService.save(domain);
        return JkyLogisticsTaskCov.INSTANCE.toBO(domain);
    }

    @Override
    public boolean update(JkyLogisticsTaskParam param, JkyLogisticsTaskQuery query) {
        return jkyLogisticsTaskService.update(
                JkyLogisticsTaskCov.INSTANCE.paramToDomain(param),
                DynamicCondition.toWrapper(query));
    }
}
