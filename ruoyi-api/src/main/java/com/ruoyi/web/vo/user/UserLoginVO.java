package com.ruoyi.web.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@ApiModel("用户登录信息")
public class UserLoginVO {

    @ApiModelProperty("用户uuid")
    private String uuid;

    @ApiModelProperty("二维码")
    private String url;

}
