package com.ruoyi.web.vo.master;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 销售渠道保证金维护请求。
 */
@Data
public class MasterSalesChannelDepositRequest {

    @NotNull(message = "销售渠道不能为空")
    private Long id;

    @NotNull(message = "保证金金额不能为空")
    @DecimalMin(value = "0.00", message = "保证金金额不能小于0")
    @Digits(integer = 16, fraction = 2, message = "保证金金额最多16位整数和2位小数")
    private BigDecimal depositAmount;
}
