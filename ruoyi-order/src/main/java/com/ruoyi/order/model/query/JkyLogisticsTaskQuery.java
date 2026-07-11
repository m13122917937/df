package com.ruoyi.order.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 吉客云物流更新延迟任务Query
 *
 * @author ruoyi
 * @date 2026-06-24
 */
@Data
@Accessors(chain = true)
public class JkyLogisticsTaskQuery {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String orderCode;

    private Integer status;

    @QueryField(operator = DynamicCondition.Operator.LTE, field = "execute_time")
    private Date leExecuteTime;
}
