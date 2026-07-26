package com.ruoyi.jky.param.sales;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 吉客云销售渠道查询请求参数。
 */
@Data
@Accessors(chain = true)
public class SalesChannelQueryParam {

    /** 页码，从 0 开始。 */
    private Integer pageIndex = 0;

    /** 每页记录数，默认 50 条。 */
    private Integer pageSize = 50;

    /** 店铺编码。 */
    private String code;

    /** 店铺名称。 */
    private String name;

    /** 修改时间起始值。 */
    private String gmtModifiedStart;

    /** 修改时间结束值。 */
    private String gmtModifiedEnd;
}
