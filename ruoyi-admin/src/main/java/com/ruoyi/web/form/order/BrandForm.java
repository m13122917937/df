package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("品牌表单")
public class BrandForm {

    @ApiModelProperty("订单状态")
    @NotNull(message = "订单状态不能为空")
    Integer status;

    @ApiModelProperty("订单状态")
    List<Integer> statusList;

    @ApiModelProperty("省id")
    private Long province;

}
