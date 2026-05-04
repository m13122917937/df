package com.ruoyi.user.model.bo;

import lombok.Data;

import java.util.Date;

/**
 * 授权信息BO
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Data
public class AuthInfoBO {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密钥
     */
    private String pwd;

    /**
     * 过期时间
     */
    private Date expired;

    /**
     * mac地址
     */
    private String macId;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 创建人id
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人id
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

}
