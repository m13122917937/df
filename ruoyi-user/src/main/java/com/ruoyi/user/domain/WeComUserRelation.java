package com.ruoyi.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 企业微信成员与系统用户关联对象 u_wecom_user_rel。
 */
@Data
@Accessors(chain = true)
@TableName("u_wecom_user_rel")
public class WeComUserRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String wecomUserId;
    private Date lastSyncTime;
    private Date createTime;
    private Date updateTime;
}
