package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class AftersalesForm {

    @ApiModelProperty(value = "订单号", example = "[\"0002\" ,\"0001\" ,  ]")
    @NotEmpty(message = "订单号不能为空")
    private List<String> orderCodeSet;


}
