package com.ruoyi.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 会员微信身份绑定。
 */
@Data
@Accessors(chain = true)
@TableName("u_member_wechat_identity")
public class MemberWechatIdentity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private String channel;
    private String appId;
    private String openId;
    private String unionId;
    private String identityStatus;
    private Date createTime;
    private Date updateTime;
}
