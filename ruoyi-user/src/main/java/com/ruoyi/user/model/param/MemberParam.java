package com.ruoyi.user.model.param;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)

public class MemberParam {

    /**
     * $column.columnComment
     */
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
     * 是否删除 默认0(0：否、1：是)
     */
    private Integer deleted;


    private Long companyId;

    private Integer owner;


}
