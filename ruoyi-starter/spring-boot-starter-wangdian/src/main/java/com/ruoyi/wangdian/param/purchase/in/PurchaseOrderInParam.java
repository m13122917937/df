package com.ruoyi.wangdian.param.purchase.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderInParam {

    /**
     * 入库单据信息（主表）
     */
    private StockinOrderHeader stockin_order;

    /**
     * 入库单明细列表
     */
    private List<StockinDetail> stockin_detail_list;

}