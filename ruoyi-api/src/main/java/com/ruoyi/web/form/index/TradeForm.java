package com.ruoyi.web.form.index;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class TradeForm {

    @ApiModelProperty("订单编码")
    @NotBlank(message = "订单编码不能为空")
    private String orderCode;

    @ApiModelProperty("挂单Id")
    @NotNull(message = "订单编码不能为空")
    private Long hangingOrderId;

    @ApiModelProperty("抢单价格")
    @NotNull(message = "抢单价格不能为空")
    private BigDecimal tradePrice;

    @ApiModelProperty("抢单价格阶梯，价4 最高价： 4 ， 价3 高价： 3 ， 价2 低价： 2， 价1 ，最低价： 1")
    @NotNull(message = "抢单价格阶梯不能为空")
    private Integer tradeIndex;

}
