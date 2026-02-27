package com.ruoyi.web.form.system;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 用户登录对象
 *
 * @author ruoyi
 */
@ApiModel(description = "企业查询实体")
@Data
public class LoginBody {
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * 唯一标识
     */
    private String uuid;


}
