package com.ruoyi.web.form.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class TransactionsQForm {

    @ApiModelProperty(value = "账户id")
    @NotNull(message = "账户id不能为空")
    private Long accountId;

    @ApiModelProperty(value = "交易对方")
    private String counterpartyLike;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date transactionDateStart;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date transactionDateEnd;


}
