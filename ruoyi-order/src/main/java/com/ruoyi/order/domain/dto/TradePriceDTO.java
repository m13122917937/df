package com.ruoyi.order.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradePriceDTO {

    private BigDecimal tradePrice;

    private Integer quantity;
}
