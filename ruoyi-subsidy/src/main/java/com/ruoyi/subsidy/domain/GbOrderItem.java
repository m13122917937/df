package com.ruoyi.subsidy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 国补商城订单商品快照。
 */
@Data
@Accessors(chain = true)
@TableName("gb_order_item")
public class GbOrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long skuId;
    private String productName;
    private String specName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountRate;
    private BigDecimal discountCapAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
}
