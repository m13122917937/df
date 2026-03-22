package com.ruoyi.web.form.user;

import com.ruoyi.common.validator.group.AddGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data

public class CompanyUserForm {


    private Long companyId;


    @NotBlank(groups = AddGroup.class, message = "用户名称不能为空")
    @Length(groups = AddGroup.class, max = 8, min = 1, message = "用户名称长度为1-8位")
    private String nickName;


    @NotBlank(groups = AddGroup.class, message = "手机号码不能为空")
    @Length(groups = AddGroup.class, max = 11, min = 11, message = "手机号码长度为11位")
    private String phone;


    @NotNull(groups = AddGroup.class, message = "是否主账号不能为空")
    private Integer owner;


}
