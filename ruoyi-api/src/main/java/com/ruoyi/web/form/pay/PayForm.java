package com.ruoyi.web.form.pay;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付入参.
 *
 */
@Data

public class PayForm implements Serializable {


    @NotNull(message = "支付金额不能为空")
    private BigDecimal amount;


    private Integer tradeType;
}
