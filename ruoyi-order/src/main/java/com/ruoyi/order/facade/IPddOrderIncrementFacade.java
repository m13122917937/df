package com.ruoyi.order.facade;

import com.ruoyi.order.model.bo.PddOrderIncrementBO;
import com.ruoyi.order.model.param.PddOrderIncrementParam;
import com.ruoyi.order.model.query.PddOrderIncrementQuery;

import java.util.List;

/**
 * 拼多多增量订单Service接口
 *
 * @author ruoyi
 * @date 2025-02-08
 */
public interface IPddOrderIncrementFacade {

    /**
     * 保存拼多多增量订单
     *
     * @param param 订单参数
     * @return 订单BO
     */
    PddOrderIncrementBO save(PddOrderIncrementParam param);

    /**
     * 更新拼多多增量订单
     *
     * @param param 更新参数
     * @param query 查询条件
     * @return 是否成功
     */
    boolean update(PddOrderIncrementParam param, PddOrderIncrementQuery query);

    /**
     * 根据订单号查询
     *
     * @param orderSn 订单号
     * @return 订单BO
     */
    PddOrderIncrementBO getByOrderSn(String orderSn);

    /**
     * 查询列表
     *
     * @param query 查询条件
     * @return 订单列表
     */
    List<PddOrderIncrementBO> list(PddOrderIncrementQuery query);

    /**
     * 保存或更新订单（根据订单号判断是否存在）
     *
     * @param param 订单参数
     * @return 订单BO
     */
    PddOrderIncrementBO saveOrUpdate(PddOrderIncrementParam param);
}