package com.ruoyi.web.form.bill;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PlanForm {



    @NotNull(message = "排款id不能为空")
    private Long id;


    @NotBlank(message = "付款凭证不能为空")
    private String payVoucherUrls;



}
