package com.ruoyi.order.facade;

import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.order.model.query.ImeiQuery;

import java.util.List;

/**
 * 订单串码Service接口
 *
 * @author ruoyi
 * @date 2025-09-11
 */
public interface IImeiFacade {

    List<ImeiBO> list(ImeiQuery query);

    ImeiBO save(ImeiParam param);

    boolean saveBatch(List<ImeiParam> params);

    ImeiBO getOne(ImeiQuery query);

    boolean update(ImeiParam param, ImeiQuery query);

    boolean delete(ImeiQuery imeiQuery);

    long count(ImeiQuery imeiQuery);

}
