package com.ruoyi.web.vo.quote;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 报价商品价格明细保存请求。
 */
@Data
public class QuoteProductPriceSaveRequest {

    /**
     * 价格档位ID
     */
    private Long tierId;

    /**
     * 价格
     */
    private BigDecimal price;
}
