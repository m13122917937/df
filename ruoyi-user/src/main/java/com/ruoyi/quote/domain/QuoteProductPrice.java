package com.ruoyi.quote.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报价商品价格明细实体，映射 quote_product_price。
 */
@Data
@TableName("quote_product_price")
public class QuoteProductPrice {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 报价商品ID
     */
    private Long productId;

    /**
     * 价格档位ID
     */
    private Long tierId;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
