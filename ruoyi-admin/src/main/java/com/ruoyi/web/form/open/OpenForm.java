package com.ruoyi.web.form.open;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OpenForm {

    @NotBlank(message = "平台不能为空")

    private String platform;

}
