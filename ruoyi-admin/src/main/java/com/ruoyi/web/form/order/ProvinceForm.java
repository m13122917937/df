package com.ruoyi.web.form.order;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data

public class ProvinceForm {


    @NotNull(message = "订单状态不能为空")
    Integer status;


    private String brand;

}
