package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("异常订单")
public class ErrorOrder {

    @ApiModelProperty("订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderCode;
    /**
     * ERROR_LOGISTICS_circulation(71, "物流无流转信息"),
     * <p>
     * ERROR_LOGISTICS_SIGN(72, "签收异常"),
     * <p>
     * ERROR_LOGISTICS_PHONE(73, "手机号后四位错误"),
     * <p>
     * ERROR_SN_ALL(74, "全部异常串码"),
     * <p>
     * ERROR_SN_LESS(75, "部分异常串码"),
     */
    @NotNull(message = "订单号不能为空")
    @ApiModelProperty("异常类型,71 物流无流转信息 ; 72   签收异常 , 73 手机号后四位错误 74 全部异常串码 75 部分异常串码  ")
    private Integer errorCode;


}
