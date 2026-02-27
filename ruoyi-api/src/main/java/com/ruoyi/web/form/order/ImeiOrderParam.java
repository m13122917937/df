package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@ApiModel("填写imei码")
public class ImeiOrderParam {

    @ApiModelProperty("订单编码")
    @NotBlank(message = "订单编码不能为空")
    private String orderCode;

    @ApiModelProperty("imei码")
    @NotNull(message = "订单编码不能为空")
    private List<String> imei;


    @ApiModelProperty("sn码")
    private String sn;

    @ApiModelProperty("imei码")
    private String imeiCode;
}
