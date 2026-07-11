package com.ruoyi.order.facade;

import com.ruoyi.order.model.bo.JkyLogisticsTaskBO;
import com.ruoyi.order.model.param.JkyLogisticsTaskParam;
import com.ruoyi.order.model.query.JkyLogisticsTaskQuery;

import java.util.List;

/**
 * 吉客云物流更新延迟任务Facade接口
 *
 * @author ruoyi
 * @date 2026-06-24
 */
public interface IJkyLogisticsTaskFacade {

    List<JkyLogisticsTaskBO> list(JkyLogisticsTaskQuery query);

    JkyLogisticsTaskBO save(JkyLogisticsTaskParam param);

    boolean update(JkyLogisticsTaskParam param, JkyLogisticsTaskQuery query);
}
