package com.ruoyi.web.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单详情VO（销售退货自动带出用）
 *
 * @author ruoyi
 * @date 2026-06-30
 */
@Data
@Accessors(chain = true)
public class SalesReturnOrderDetailVO {

    /** 内部单号 */
    private String orderCode;

    /** 商家单号 */
    private String originalOrderId;

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

    /** 订单数量 */
    private Long quantity;

    /** 成交价 */
    private BigDecimal tradePrice;

    /** 供应商ID */
    private Long companyId;

    /** 供应商名称 */
    private String companyName;

    /** 店铺 */
    private String shopName;

    /** 下单时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
