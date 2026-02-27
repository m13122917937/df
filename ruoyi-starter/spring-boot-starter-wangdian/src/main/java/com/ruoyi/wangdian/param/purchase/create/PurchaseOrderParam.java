package com.ruoyi.wangdian.param.purchase.create;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderParam {

    /**
     * 采购单编号
     */
    private String purchase_no;

    /**
     * 供应商编号
     */
    private String provider_no;

    /**
     * 收货仓编号
     */
    private String receive_warehouse_nos;

    /**
     * 预计入库仓库编号
     */
    private String expect_warehouse_no;

    /**
     * 采购员
     */
    private String purchaserer_name;

    /**
     * 采购单详情列表
     */
    private List<PurchaseDetailParam> purchase_details;
}