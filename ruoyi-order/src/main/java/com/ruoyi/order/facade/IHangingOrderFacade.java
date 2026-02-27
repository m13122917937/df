package com.ruoyi.order.facade;

import com.ruoyi.order.model.bo.HangingOrderBO;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.query.HangingOrderQuery;

import java.util.List;

/**
 * 挂单Service接口
 *
 * @author ruoyi
 * @date 2025-09-09
 */
public interface IHangingOrderFacade {

    List<HangingOrderBO> list(HangingOrderQuery query);

    HangingOrderBO getOne(HangingOrderQuery query);

    long count(HangingOrderQuery query);

    boolean update(HangingOrderParam param, HangingOrderQuery query);

    HangingOrderBO save(HangingOrderParam hangingOrderParam);
}
