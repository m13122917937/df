package com.ruoyi.web.form.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("公司资金流水参数")
public class CompanyCapitalLogForm {

    @ApiModelProperty(hidden = true)
    private Long companyId;


    @ApiModelProperty(value ="开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date startDate;

    @ApiModelProperty(value ="开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date endDate;


    @ApiModelProperty(value ="流水类型")
    private Integer type;

}
