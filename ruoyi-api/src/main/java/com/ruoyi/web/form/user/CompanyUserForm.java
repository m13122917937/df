package com.ruoyi.web.form.user;

import com.ruoyi.common.validator.group.AddGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("企业用户")
public class CompanyUserForm {

    @ApiModelProperty(value = "企业id", required = false, hidden = true)
    private Long companyId;

    @ApiModelProperty("用户名称")
    @NotBlank(groups = AddGroup.class, message = "用户名称不能为空")
    @Length(groups = AddGroup.class, max = 8, min = 1, message = "用户名称长度为1-8位")
    private String nickName;

    @ApiModelProperty("手机号码")
    @NotBlank(groups = AddGroup.class, message = "手机号码不能为空")
    @Length(groups = AddGroup.class, max = 11, min = 11, message = "手机号码长度为11位")
    private String phone;

    @ApiModelProperty("是否主账号，0 主账号 1非主账号 ")
    @NotNull(groups = AddGroup.class, message = "是否主账号不能为空")
    private Integer owner;


}
