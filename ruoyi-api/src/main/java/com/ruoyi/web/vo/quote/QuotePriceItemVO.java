package com.ruoyi.web.vo.quote;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 批发报价页商品价格明细响应。
 */
@Data
public class QuotePriceItemVO {

    /**
     * 价格档位ID
     */
    private Long tierId;

    /**
     * 价格
     */
    private BigDecimal price;
}
