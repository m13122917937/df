package com.ruoyi.order.model.bo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradePriceBO {

    private BigDecimal tradePrice;

    private Integer quantity;
}
