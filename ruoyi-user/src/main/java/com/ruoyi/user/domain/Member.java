package com.ruoyi.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 会员信息对象 u_member
 *
 * @author ruoyi
 * @date 2025-09-06
 */
@Data
@Accessors(chain = true)
@TableName("u_member")
public class Member {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

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
     * 是否删除 默认0(0：否、1：是)
     */
    private Integer deleted;


}
