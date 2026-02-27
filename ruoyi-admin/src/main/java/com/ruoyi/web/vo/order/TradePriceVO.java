package com.ruoyi.web.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("订单价格")
public class TradePriceVO {

    @ApiModelProperty("价格")
    private BigDecimal tradePrice;

    @ApiModelProperty("数量")
    private Integer quantity;

}
