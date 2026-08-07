package com.ruoyi.quote.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Collection;

/**
 * 报价流水查询条件。
 */
@Data
@Accessors(chain = true)
public class QuotePriceHistoryQuery {

    /**
     * 报价商品ID
     */
    @QueryField(field = "product_id")
    private Long productId;

    /**
     * 报价商品ID集合
     */
    @QueryField(operator = DynamicCondition.Operator.IN, field = "product_id")
    private Collection<Long> productIds;

    /**
     * 报价日期（精确匹配）
     */
    @QueryField(field = "quote_date")
    private LocalDate quoteDate;

    /**
     * 返回条数限制（LIMIT）
     */
    @QueryField(operator = DynamicCondition.Operator.LIMIT)
    private Integer limit;
}
