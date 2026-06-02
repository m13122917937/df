package com.ruoyi.web.form.order;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PlatformImeiForm {

    @NotBlank(message = "订单号不能为空")
    private String orderCode;

    private String sn;

    private String imei;

    @NotNull(message = "平台校验状态不能为空")
    private Integer status;

}
