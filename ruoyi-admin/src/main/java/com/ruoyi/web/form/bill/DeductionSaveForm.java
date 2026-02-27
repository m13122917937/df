package com.ruoyi.web.form.bill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel
public class DeductionSaveForm {

    @ApiModelProperty("订单编号")
    private String orderCode;

    @ApiModelProperty("扣罚金额")
    private BigDecimal amount;

    @ApiModelProperty("扣罚原因")
    private String remark;
}
