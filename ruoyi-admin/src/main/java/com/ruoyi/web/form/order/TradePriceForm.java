package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiOperation("查询报价")

@Data
public class TradePriceForm {

    @ApiModelProperty("订单编号")
    @NotBlank(message = "订单编号不能为空")
    private String orderCode;

    @ApiModelProperty("省id")
    @NotNull(message = "省id不能为空")
    private Long province;
}
