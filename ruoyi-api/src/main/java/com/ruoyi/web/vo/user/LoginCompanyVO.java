package com.ruoyi.web.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel("登录公司")
public class LoginCompanyVO {

    @ApiModelProperty("是否登录成功")
    private Boolean isLogin;

    @ApiModelProperty("是否新用户")
    private Boolean newUser;

    @ApiModelProperty("登录的uuid")
    private String uuid;

    @ApiModelProperty("登录用户名")
    private String userName;

    @ApiModelProperty("登录成功后的公司列表")
    private List<CompanyVO> companyVOList;

    @ApiModelProperty("登录成功后的角色列表")
    private String roles;



}
