package com.ruoyi.web.form.open;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OpenForm {

    @NotBlank(message = "平台不能为空")
    @ApiModelProperty("平台")
    private String platform;

}
