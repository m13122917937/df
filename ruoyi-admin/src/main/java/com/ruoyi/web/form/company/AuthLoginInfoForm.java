package com.ruoyi.web.form.company;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 授权登录查询表单
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Data
@Accessors(chain = true)
public class AuthLoginInfoForm {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户名
     */
    private String loginName;

    /**
     * 密钥
     */
    private String pwd;

    /**
     * mac地址
     */
    private String macId;

}
