package com.ruoyi.web.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data

public class UserVO {


    /**
     * 手机号码
     */

    private String phone;

    /**
     * 昵称
     */

    private String nickName;



    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    private Integer owner;


}

