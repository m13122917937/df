package com.ruoyi.web.form.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售退货保存表单
 *
 * @author ruoyi
 * @date 2026-06-30
 */
@Data
public class SalesReturnSaveForm {

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

    /** 退货日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date returnDate;

    /** 备注 */
    private String remark;
}
