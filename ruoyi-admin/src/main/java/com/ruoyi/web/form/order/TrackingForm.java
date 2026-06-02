package com.ruoyi.web.form.order;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TrackingForm {

    @NotBlank(message = "订单号不能为空")
    private String orderCode;

    private String trackingCompany;

    private String trackingNumber;

}
