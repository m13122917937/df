package com.ruoyi.jky.param.company;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 吉客云公司分页查询请求参数。
 */
@Data
@Accessors(chain = true)
public class CompanyQueryParam {

    /** 页码，从 0 开始。 */
    private Integer pageIndex = 0;

    /** 每页记录数，默认 50 条。 */
    private Integer pageSize = 50;
}
