package com.ruoyi.quote.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collection;

/**
 * 报价商品价格明细查询条件。
 */
@Data
@Accessors(chain = true)
public class QuoteProductPriceQuery {

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
     * 价格档位ID
     */
    @QueryField(field = "tier_id")
    private Long tierId;
}
