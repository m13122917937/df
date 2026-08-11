package com.ruoyi.subsidy.model.query;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 国补分类查询条件。
 */
@Data
@Accessors(chain = true)
public class GbCategoryQuery {
    private Long id;
    private Long parentId;
    private Integer status;
}
