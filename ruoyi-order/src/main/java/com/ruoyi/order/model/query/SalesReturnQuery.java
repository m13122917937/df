package com.ruoyi.order.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 销售退货单查询对象 o_sales_return
 *
 * @author ruoyi
 * @date 2026-06-30
 */
@Data
@Accessors(chain = true)
public class SalesReturnQuery {
    private static final long serialVersionUID = 1L;

    private Long id;

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
    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "category")
    private String category;

    /** 商品名/型号 */
    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "product_name")
    private String productNameLike;

    /** 规格名 */
    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "sku_name")
    private String skuNameLike;

    /** 退货日期 */
    private Date returnDate;

    /** 状态 */
    private Integer status;

    /** 删除标志 */
    private Integer deleted;
}
