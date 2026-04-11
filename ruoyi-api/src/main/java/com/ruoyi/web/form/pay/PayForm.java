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

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 订单描述
     */
    private String body;

    /**
     * 客户端IP
     */
    private String clientIp;

}
