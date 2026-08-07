package com.ruoyi.quote.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 报价商品查询条件。
 */
@Data
@Accessors(chain = true)
public class QuoteProductQuery {

    /**
     * 品牌（精确匹配）
     */
    @QueryField(field = "brand")
    private String brand;

    /**
     * 品类（精确匹配）
     */
    @QueryField(field = "category")
    private String category;

    /**
     * 商品名（模糊匹配）
     */
    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "product_name")
    private String productNameLike;

    /**
     * 规格/型号（模糊匹配）
     */
    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "spec_name")
    private String specNameLike;
}
