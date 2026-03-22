package com.ruoyi.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data

public class UserVO {

    private Long userId;


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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

//    /**
//     * 是否禁用 默认0(0：否、1：是)
//     */
//
//    private Integer disable;

//    /**
//     * 是否删除 默认0(0：否、1：是)
//     */
//
//    private Integer deleted;


    private Integer owner;


}
