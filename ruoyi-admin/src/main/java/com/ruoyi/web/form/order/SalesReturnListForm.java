package com.ruoyi.web.form.order;

import lombok.Data;

/**
 * 销售退货列表查询表单
 *
 * @author ruoyi
 * @date 2026-06-30
 */
@Data
public class SalesReturnListForm {

    /** 退货单号 */
    private String returnCode;

    /** 原订单内部单号 */
    private String orderCode;

    /** 原商家单号 */
    private String originalOrderId;

    /** 供应商ID */
    private Long companyId;

    /** 品牌 */
    private String brand;

    /** 品类 */
    private String category;

    /** 商品名/型号 */
    private String productNameLike;

    /** 规格名 */
    private String skuNameLike;
}
