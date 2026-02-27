package com.ruoyi.web.form.bill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class SplitForm {

    @ApiModelProperty("账单id集合")
    @NotEmpty(message = "请选择账单")
    private Set<Long> billIds;

    @ApiModelProperty("付款主体id")
    @NotNull(message = "请选择付款主体")
    private Long payerId;

    @NotNull(message = "请选择供应商银行")
    @ApiModelProperty("供应商银行id")
    private Long supplierBankId;

}
