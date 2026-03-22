package com.ruoyi.web.form.order;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data

public class CancelApplyForm {


    @NotBlank(message = "订单编码不能为空")
    private String orderCode;


    private String cancelReason;


    @NotNull(message = "申请类型不能为空")
    private Integer applyType;
}

