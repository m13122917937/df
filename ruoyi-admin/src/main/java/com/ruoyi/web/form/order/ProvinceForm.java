package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("省表单")
public class ProvinceForm {

    @ApiModelProperty("订单状态")
    @NotNull(message = "订单状态不能为空")
    Integer status;

    @ApiModelProperty("品牌")
    private String brand;

}
