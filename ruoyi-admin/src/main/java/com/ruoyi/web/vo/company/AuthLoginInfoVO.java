package com.ruoyi.web.vo.company;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 授权登录信息VO
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Data
@Accessors(chain = true)
public class AuthLoginInfoVO {

    /**
     * 授权类型
     */
    private Integer type;

    /**
     * 用户名
     */
    private String userName;

    /**
     * mac地址
     */
    private String macId;

    /**
     * 过期时间
     */
    private String expired;

}
