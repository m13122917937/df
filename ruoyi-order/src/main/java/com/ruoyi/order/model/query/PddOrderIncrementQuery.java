package com.ruoyi.order.model.query;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 拼多多增量订单查询对象
 *
 * @author ruoyi
 * @date 2025-02-08
 */
@Data
@Accessors(chain = true)
public class PddOrderIncrementQuery {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 付款主体ID
     */
    private Long payerConfigId;

    /**
     * 付款主体名称
     */
    private String payerName;
}