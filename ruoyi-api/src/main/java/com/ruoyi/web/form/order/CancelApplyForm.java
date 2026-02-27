package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("毁单申请")
public class CancelApplyForm {

    @ApiModelProperty("订单编码")
    @NotBlank(message = "订单编码不能为空")
    private String orderCode;

    @ApiModelProperty("毁单原因")
    private String cancelReason;

    @ApiModelProperty("毁单申请，1立即毁单，2免责毁单")
    @NotNull(message = "申请类型不能为空")
    private Integer applyType;
}

