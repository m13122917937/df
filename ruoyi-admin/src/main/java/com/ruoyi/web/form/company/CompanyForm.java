package com.ruoyi.web.form.company;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "企业查询实体")
@Data
public class CompanyForm {

    @ApiModelProperty("企业名称模糊查询")
    private String companyNameLike;

    @ApiModelProperty("联系人昵称模糊查询")
    private String nickNameLike;

    @ApiModelProperty("企业省1查询")
    private Long provinceId;


    @ApiModelProperty("企业市查询")
    private Long cityId;

}
