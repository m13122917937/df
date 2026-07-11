package com.ruoyi.order.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售退货单对象 o_sales_return
 *
 * @author ruoyi
 * @date 2026-06-30
 */
@Data
@Accessors(chain = true)
public class SalesReturnBO {
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

    /** 供应商名称 */
    private String companyName;

    /** 品牌 */
    private String brand;

    /** 品类 */
    private String category;

    /** 商品名/型号 */
    private String productName;

    /** 规格名 */
    private String skuName;

    /** SKU编码 */
    private String skuCode;

    /** 退货数量 */
    private Integer quantity;

    /** 原订单数量 */
    private Integer originalQuantity;

    /** 退货单价 */
    private BigDecimal returnPrice;

    /** 总金额 */
    private BigDecimal totalAmount;

    /** 退货日期 */
    private Date returnDate;

    /** 状态:0=已创建,1=已扣款 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;

    /** 删除标志 */
    private Integer deleted;
}
