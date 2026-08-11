package com.ruoyi.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 会员微信身份冲突记录。
 */
@Data
@Accessors(chain = true)
@TableName("u_member_wechat_identity_conflict")
public class MemberWechatIdentityConflict {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String unionId;
    private Long sourceMemberId;
    private Long targetMemberId;
    private String conflictStatus;
    private String handleRemark;
    private Date createTime;
    private Date updateTime;
}
