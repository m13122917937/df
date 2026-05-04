package com.ruoyi.user.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 授权信息参数
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Data
@Accessors(chain = true)
public class AuthInfoParam {

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
     * 更新人id
     */
    private Long updateBy;

}
