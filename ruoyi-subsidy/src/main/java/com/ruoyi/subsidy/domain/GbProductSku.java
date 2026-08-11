package com.ruoyi.subsidy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 国补商城 SKU。
 */
@Data
@Accessors(chain = true)
@TableName("gb_product_sku")
public class GbProductSku {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String skuCode;
    private String specName;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private Integer salesQuantity;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
