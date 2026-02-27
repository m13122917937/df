package com.ruoyi.web.form.bill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PlanForm {


    @ApiModelProperty("排款id")
    @NotNull(message = "排款id不能为空")
    private Long id;

    @ApiModelProperty("付款凭证")
    @NotBlank(message = "付款凭证不能为空")
    private String payVoucherUrls;



}
