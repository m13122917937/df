package com.ruoyi.web.form.index;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class TradeForm {


    @NotBlank(message = "订单编码不能为空")
    private String orderCode;


    @NotNull(message = "订单编码不能为空")
    private Long hangingOrderId;


    @NotNull(message = "抢单价格不能为空")
    private BigDecimal tradePrice;


    @NotNull(message = "抢单价格阶梯不能为空")
    private Integer tradeIndex;

}
