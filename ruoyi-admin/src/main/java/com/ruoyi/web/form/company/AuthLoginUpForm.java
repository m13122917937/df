package com.ruoyi.web.form.company;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 授权登录更新表单
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Data
@Accessors(chain = true)
public class AuthLoginUpForm {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 登录用户名
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

    /**
     * 过期时间
     */
    private Date expired;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 签名
     */
    private String sig;

}
