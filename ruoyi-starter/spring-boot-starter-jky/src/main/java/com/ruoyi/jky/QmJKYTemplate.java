package com.ruoyi.jky;

import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.param.order.OrderQueryParam;
import com.ruoyi.jky.properties.JkyProperties;
import com.ruoyi.jky.rep.order.OrderQueryRep;
import lombok.extern.slf4j.Slf4j;

/**
 * 启明吉客云模板。
 */
@Slf4j
public class QmJKYTemplate extends JkyTemplate {

    /**
     * 创建 QM JKY 模板实例。
     */
    public QmJKYTemplate(final JkyProperties jkyProperties) {
        super(jkyProperties);
    }

    /**
     * 查询销售单。
     */
    @Override
    public JkyResponse<OrderQueryRep> queryOrders(final OrderQueryParam param) {
        return new JkyResponse<>();
    }

}