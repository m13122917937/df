package com.ruoyi.user.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class CompanyUserDTO {

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * 微信
     */
    private String unionId;

    /**
     * 微信
     */
    private String openId;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否禁用 默认0(0：否、1：是)
     */
    private Integer disable;

    /**
     * 是否删除 默认0(0：否、1：是)
     */
    private Integer deleted;


    /**
     * 是否负责人
     */
    private Integer owner;
}
