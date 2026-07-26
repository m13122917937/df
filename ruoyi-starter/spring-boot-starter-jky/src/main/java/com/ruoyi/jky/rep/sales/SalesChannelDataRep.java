package com.ruoyi.jky.rep.sales;

import lombok.Data;

import java.util.List;

/**
 * 吉客云销售渠道查询响应数据。
 */
@Data
public class SalesChannelDataRep {

    /**
     * 销售渠道列表。
     */
    private List<SalesChannelRep> salesChannelInfo;

}
