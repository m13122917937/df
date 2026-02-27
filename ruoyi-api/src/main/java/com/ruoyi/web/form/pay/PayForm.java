package com.ruoyi.web.form.pay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付入参.
 *
 */
@Data
@ApiModel("支付参数")
public class PayForm implements Serializable {

    @ApiModelProperty("支付金额 必须大于50元")
    @NotNull(message = "支付金额不能为空")
    private BigDecimal amount;

    @ApiModelProperty("支付类型 1.二维码支付 2.JSAPI支付 3.小程序支付")
    private Integer tradeType;
}
