package com.ruoyi.web.form.order;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;



@Data
public class TradePriceForm {


    @NotBlank(message = "订单编号不能为空")
    private String orderCode;


    @NotNull(message = "省id不能为空")
    private Long province;
}
