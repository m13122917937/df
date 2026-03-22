package com.ruoyi.web.form.bill;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data

public class DeductionSaveForm {


    private String orderCode;


    private BigDecimal amount;


    private String remark;
}
