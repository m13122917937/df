package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("撤销订单报价")
public class RevokeHangingForm {


    @ApiModelProperty(value = "订单号", example = "[\"0002\" ,\"0001\" ,  ]")
    private List<String> orderCode;


}
