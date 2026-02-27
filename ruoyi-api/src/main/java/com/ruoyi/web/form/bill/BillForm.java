package com.ruoyi.web.form.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class BillForm {

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date startDate;


    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date endDate;


    @ApiModelProperty(value = "供应商id", hidden = true)
    private Long supplierId;
}
