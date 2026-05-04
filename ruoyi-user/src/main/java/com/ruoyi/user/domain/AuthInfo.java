package com.ruoyi.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 授权信息对象 u_auth_info
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Data
@Accessors(chain = true)
@TableName("u_auth_info")
public class AuthInfo {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
