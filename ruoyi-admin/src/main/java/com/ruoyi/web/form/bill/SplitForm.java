package com.ruoyi.web.form.bill;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class SplitForm {


    @NotEmpty(message = "请选择账单")
    private Set<Long> billIds;


    @NotNull(message = "请选择付款主体")
    private Long payerId;

    @NotNull(message = "请选择供应商银行")

    private Long supplierBankId;

}
