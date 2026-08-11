package com.ruoyi.subsidy.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 国补商品查询条件。
 */
@Data
@Accessors(chain = true)
public class GbProductQuery {
    private Long id;
    private Long categoryId;
    private Integer status;
    private Integer recommended;
    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "product_name")
    private String productNameLike;
}
