package com.ruoyi.web.form.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class TransactionsQForm {


    @NotNull(message = "账户id不能为空")
    private Long accountId;


    private String counterpartyLike;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date transactionDateStart;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date transactionDateEnd;


}
